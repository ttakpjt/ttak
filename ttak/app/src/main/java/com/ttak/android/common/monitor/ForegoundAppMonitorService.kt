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
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ForegroundMonitorService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var usageStatsManager: UsageStatsManager
    private var isMonitoring = false
    private lateinit var repository: FocusGoalRepository
    private var lastEventTime: Long = 0L
    private val observerApi by lazy {
        ObserverApiImpl.getInstance(this)
    }

    // 상태를 enum으로 정의
    enum class AppState(val value: Int) {
        NORMAL(0),
        RESTRICTED(1);

        companion object {
            fun fromValue(value: Int) = values().find { it.value == value } ?: NORMAL
        }
    }

    // 현재 상태를 non-null로 관리
    private var currentState: AppState = AppState.NORMAL

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "ForegroundMonitorChannel"
        private const val TAG = "ForegroundAppMonitor"
        private const val EVENT_CHECK_INTERVAL = 500L // 0.5초
    }

    override fun onCreate() {
        super.onCreate()
        initializeService()
    }

    private fun initializeService() {
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // Initialize Repository
        val database = AppDatabase.getDatabase(applicationContext)
        repository = FocusGoalRepository(
            focusGoalDao = database.focusGoalDao(),
            selectedAppDao = database.selectedAppDao(),
            packageManager = applicationContext.packageManager
        )

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMonitoring()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

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

    private fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true

        serviceScope.launch {
            while (isMonitoring) {
                checkForNewEvents()
                delay(EVENT_CHECK_INTERVAL)
            }
        }
    }

    private fun checkForNewEvents() {
        if (!hasUsageStatsPermission()) {
            Log.e(TAG, "Usage stats permission not granted!")
            return
        }

        val currentTime = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(lastEventTime, currentTime)
        val event = android.app.usage.UsageEvents.Event()

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND) {
                lastEventTime = event.timeStamp
                Log.d(TAG, "App switch detected: ${event.packageName}")
                checkFocusGoalAndUpdateStatus(event.packageName)
            }
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val currentTime = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            currentTime - 60_000, // 1분 전부터
            currentTime
        )
        return !stats.isNullOrEmpty()
    }

    private fun checkFocusGoalAndUpdateStatus(packageName: String) {
        serviceScope.launch {
            val currentTime = LocalDateTime.now()
            Log.d(TAG, "Checking focus goals at: $currentTime")

            repository.getAllGoals().collect { goals ->
                val newState = determineAppState(goals, packageName, currentTime)
                updateStateIfChanged(newState)
            }
        }
    }

    private fun determineAppState(
        goals: List<FocusGoal>,
        packageName: String,
        currentTime: LocalDateTime
    ): AppState {
        val activeGoal = goals.firstOrNull { goal ->
            goal.isEnabled &&
                    currentTime.isAfter(goal.startDateTime) &&
                    currentTime.isBefore(goal.endDateTime)
        }

        if (activeGoal == null) {
            Log.d(TAG, "No active goal found")
            return AppState.NORMAL
        }

        return if (activeGoal.selectedApps.any { it.packageName == packageName }) {
            Log.d(TAG, "Package $packageName is restricted")
            AppState.RESTRICTED
        } else {
            Log.d(TAG, "Package $packageName is normal")
            AppState.NORMAL
        }
    }

    private suspend fun updateStateIfChanged(newState: AppState) {
        if (currentState != newState) {
            Log.d(TAG, "State changing from ${currentState.name} to ${newState.name}")
            currentState = newState
            observerApi.updateMyStatus(newState.value)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isMonitoring = false
        serviceScope.cancel()
    }
}