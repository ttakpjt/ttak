package com.ttak.android.common.monitor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ttak.android.data.local.AppDatabase
import com.ttak.android.data.repository.FocusGoalRepository
import com.ttak.android.domain.model.FocusGoal
import com.ttak.android.network.implementation.ObserverApiImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDateTime

class ForegroundMonitorService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var usageStatsManager: UsageStatsManager
    private var isMonitoring = false
    private lateinit var repository: FocusGoalRepository
    private var lastEventTime: Long = 0L

    private var currentForegroundPackage: String? = null
    private val stateMutex = Mutex()
    private var currentState: AppState = AppState.NORMAL
    private var lastStateUpdateTime: Long = 0
    private var pendingStateChange: Boolean = false

    private val observerApi by lazy {
        ObserverApiImpl.getInstance(this)
    }

    enum class AppState(val value: Int) {
        NORMAL(0),
        RESTRICTED(1);

        companion object {
            fun fromValue(value: Int) = values().find { it.value == value } ?: NORMAL
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "ForegroundMonitorChannel"
        private const val TAG = "ForegroundAppMonitor"
        private const val EVENT_CHECK_INTERVAL = 500L
        private const val STATE_UPDATE_THRESHOLD = 1000L

        private val LAUNCHER_PACKAGES = setOf(
            "com.google.android.apps.nexuslauncher",
            "com.android.launcher3",
            "com.android.launcher",
            "com.android.launcher2"
        )
    }

    override fun onCreate() {
        super.onCreate()
        initializeService()
        startMonitoring()
    }

    private fun initializeService() {
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val database = AppDatabase.getDatabase(applicationContext)
        repository = FocusGoalRepository(
            focusGoalDao = database.focusGoalDao(),
            selectedAppDao = database.selectedAppDao(),
            packageManager = applicationContext.packageManager
        )

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground App Monitor",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitoring Service Running"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("App Monitoring")
        .setContentText("Running in background")
        .setSmallIcon(android.R.drawable.ic_menu_info_details)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMonitoring()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startMonitoring() {
        if (isMonitoring) {
            Log.d(TAG, "Monitoring already active")
            return
        }

        isMonitoring = true
        Log.d(TAG, "Starting monitoring service")

        serviceScope.launch {
            while (isMonitoring) {
                checkForNewEvents()
                delay(EVENT_CHECK_INTERVAL)
            }
        }
    }

    private fun checkForNewEvents() {
        if (!hasUsageStatsPermission()) return

        try {
            val currentTime = System.currentTimeMillis()
            if (lastEventTime == 0L) {
                lastEventTime = currentTime - 1000
            }

            val events = usageStatsManager.queryEvents(lastEventTime, currentTime)
            val event = android.app.usage.UsageEvents.Event()

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (event.eventType == android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    lastEventTime = event.timeStamp

                    val packageName = event.packageName
                    Log.d(TAG, "App switch detected: $packageName")

                    if (currentForegroundPackage != packageName) {
                        serviceScope.launch {
                            processAppSwitch(packageName)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking events", e)
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val currentTime = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            currentTime - 60_000,
            currentTime
        )
        return !stats.isNullOrEmpty()
    }

    private suspend fun processAppSwitch(newPackage: String) {
        stateMutex.withLock {
            if (pendingStateChange) {
                Log.d(TAG, "Waiting for pending state change to complete")
                return
            }

            try {
                pendingStateChange = true
                val currentTime = System.currentTimeMillis()

                // 마지막 상태 업데이트로부터 충분한 시간이 지났는지 확인
                if ((currentTime - lastStateUpdateTime) < STATE_UPDATE_THRESHOLD) {
                    Log.d(TAG, "Too soon for state update, waiting...")
                    return
                }

                currentForegroundPackage = newPackage
                val goals = repository.getAllGoals().first()
                val currentDateTime = LocalDateTime.now()

                val newState = when {
                    LAUNCHER_PACKAGES.contains(newPackage) -> {
                        Log.d(TAG, "Launcher package detected, setting NORMAL state")
                        AppState.NORMAL
                    }
                    else -> determineAppState(goals, newPackage, currentDateTime)
                }

                if (currentState != newState) {
                    Log.d(TAG, """
                        State change:
                        Previous package: $currentForegroundPackage
                        New package: $newPackage
                        From state: ${currentState.name}
                        To state: ${newState.name}
                        Is launcher: ${LAUNCHER_PACKAGES.contains(newPackage)}
                        Active goals: ${goals.count { it.isEnabled }}
                    """.trimIndent())

                    currentState = newState
                    lastStateUpdateTime = currentTime

                    try {
                        observerApi.updateMyStatus(newState.value)
                        Log.d(TAG, "State updated to: ${newState.name}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to update state", e)
                    }
                } else {
                    Log.d(TAG, "State unchanged: ${currentState.name} for package: $newPackage")
                }
            } finally {
                pendingStateChange = false
            }
        }
    }

    private suspend fun determineAppState(
        goals: List<FocusGoal>,
        packageName: String,
        currentTime: LocalDateTime
    ): AppState {
        val activeGoals = goals.filter { goal ->
            goal.isEnabled &&
                    currentTime.isAfter(goal.startDateTime) &&
                    currentTime.isBefore(goal.endDateTime)
        }

        if (activeGoals.isEmpty()) {
            Log.d(TAG, "No active goals")
            return AppState.NORMAL
        }

        val isRestricted = activeGoals.any { goal ->
            goal.selectedApps.any { app ->
                val isMatched = app.packageName == packageName
                if (isMatched) {
                    Log.d(TAG, "Package $packageName is restricted by goal: ${goal.id}")
                }
                isMatched
            }
        }

        return if (isRestricted) {
            Log.d(TAG, "Package $packageName is RESTRICTED")
            AppState.RESTRICTED
        } else {
            Log.d(TAG, "Package $packageName is NORMAL")
            AppState.NORMAL
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service being destroyed")
        isMonitoring = false
        serviceScope.cancel()
    }
}
