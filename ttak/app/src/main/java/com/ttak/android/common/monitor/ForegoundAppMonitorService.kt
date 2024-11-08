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
import com.ttak.android.network.implementation.ObserverApiImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.time.LocalDateTime

class ForegroundMonitorService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var usageStatsManager: UsageStatsManager
    private var isMonitoring = false
    private val webSocketManager = WebSocketManager.getInstance()
    private lateinit var repository: FocusGoalRepository
    private var previousStatus: Int? = null // 이전 상태를 저장하는 변수
    private var lastEventTime: Long = 0L
    private val observerApi = ObserverApiImpl.getInstance()

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "ForegroundMonitorChannel"
        private const val TAG = "ForegroundAppMonitor"
    }

    override fun onCreate() {
        super.onCreate()
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

//    private fun startMonitoring() {
//        if (isMonitoring) return
//        isMonitoring = true
//
//        serviceScope.launch {
//            while (isMonitoring) {
//                checkForegroundApp()
////                sendApiRequest()
//                delay(2000) // 2초마다 체크
//            }
//        }
//    }

    // 이벤트 기반 모니터링
    private fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true

        serviceScope.launch {
            while (isMonitoring) {
                checkForNewEvents()
                delay(500) // 0.5초 간격으로 새로운 이벤트 체크
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
                // 이벤트 발생 시간 업데이트
                lastEventTime = event.timeStamp
                Log.d(TAG, "App switch detected: ${event.packageName}")
                checkFocusGoalAndUpdateStatus(event.packageName)
            }
        }
    }



    private fun checkForegroundApp() {
        // 권한 체크 추가
        if (!hasUsageStatsPermission()) {
            Log.e(TAG, "Usage stats permission not granted!")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val time = System.currentTimeMillis()
            val usageEvents = usageStatsManager.queryEvents(
                time - 1000 * 60,
                time
            )

            var foregroundApp: String? = null
            val event = android.app.usage.UsageEvents.Event()

            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.eventType == android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    foregroundApp = event.packageName
                }
            }

            foregroundApp?.let { packageName ->
                Log.d(TAG, "Current foreground app: $packageName")
                checkFocusGoalAndUpdateStatus(packageName)
            } ?: Log.d(TAG, "No foreground app detected")
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val time = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            time - 1000 * 60,  // 1분 전부터
            time
        )
        return stats != null && stats.isNotEmpty()
    }

    private fun checkFocusGoalAndUpdateStatus(packageName: String) {
        serviceScope.launch {
            val currentTime = LocalDateTime.now()
            Log.d(TAG, "Checking focus goals at: $currentTime")

            repository.getAllGoals().collect { goals ->
                val activeGoal = goals.firstOrNull { goal ->
                    goal.isEnabled &&
                            currentTime.isAfter(goal.startDateTime) &&
                            currentTime.isBefore(goal.endDateTime)
                }

                if (activeGoal == null) {
                    Log.d(TAG, "No active goal found")
                    // 이전에 제한 앱을 사용 중이었다면 상태 변경
                    if (previousStatus == 1) {
                        previousStatus = 0
                        observerApi.updateMyStatus(0)
                        Log.d(TAG, "Changed from restricted to normal app")
                    }
                    return@collect
                }

                val isSelectedApp = activeGoal.selectedApps.any { it.packageName == packageName }
                if (isSelectedApp) {
                    // 일반 앱 -> 제한 앱으로 변경된 경우
                    if (previousStatus == 0 || previousStatus == null) {
                        Log.d(TAG, "Switched to restricted app: $packageName")
                        previousStatus = 1
                        observerApi.updateMyStatus(1)
                    }
                } else {
                    // 제한 앱 -> 일반 앱으로 변경된 경우
                    if (previousStatus == 1) {
                        Log.d(TAG, "Switched to normal app: $packageName")
                        previousStatus = 0
                        observerApi.updateMyStatus(0)
                    }
                }
            }
        }
    }

    // API 요청을 보내는 함수
    private suspend fun sendApiRequest() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://k11a509.p.ssafy.io/api/user/test/1") // 올바른 API URL로 변경
            .build()

        try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d(TAG, "API 요청 성공: ${response.body?.string()}")
            } else {
                Log.e(TAG, "API 요청 실패: ${response.code}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "API 요청 오류: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isMonitoring = false
        serviceScope.cancel() // 코루틴 스코프 정리
    }
}
