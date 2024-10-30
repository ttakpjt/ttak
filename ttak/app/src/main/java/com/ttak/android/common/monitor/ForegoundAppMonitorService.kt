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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
- 실제 백그라운드에서 실행되는 서비스
- 포그라운드 앱 모니터링 수행
- 알림 표시 및 로그 출력
 */

class ForegroundMonitorService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var usageStatsManager: UsageStatsManager
    private var isMonitoring = false

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "ForegroundMonitorChannel"
        private const val TAG = "ForegroundAppMonitor"
    }

    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
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
                description = "모니터링 서비스 실행 중"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("앱 모니터링")
        .setContentText("백그라운드에서 실행 중")
        .setSmallIcon(android.R.drawable.ic_menu_info_details)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

    private fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true

        serviceScope.launch {
            while (isMonitoring) {
                checkForegroundApp()
                delay(2000) // 2초마다 체크
            }
        }
    }

    private fun checkForegroundApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val time = System.currentTimeMillis()
            val usageEvents = usageStatsManager.queryEvents(
                time - 1000 * 60, // 1분 전부터
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

            foregroundApp?.let {
                Log.d(TAG, "Current foreground app: $it")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isMonitoring = false
    }
}