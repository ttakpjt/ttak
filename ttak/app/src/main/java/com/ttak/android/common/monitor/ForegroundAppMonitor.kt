package com.ttak.android.common.monitor

import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import java.util.concurrent.TimeUnit

/*
- Usage Stats 권한 확인 및 요청을 담당
- MainActivity에서 사용
- 주로 권한 관련 기능 처리
 */

class ForegroundAppMonitor(private val application: Application) {
    private lateinit var usageStatsManager: UsageStatsManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsManager = application.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        }
    }

    fun hasUsageStatsPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val time = System.currentTimeMillis()
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                time - TimeUnit.DAYS.toMillis(1),
                time
            )
            return stats != null && stats.isNotEmpty()
        }
        return false
    }

    fun requestUsageStatsPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}