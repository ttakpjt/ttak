package com.ttak.android.common.monitor

import android.app.Application
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings

class ForegroundAppMonitor(private val application: Application) {

    fun hasUsageStatsPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false
        }

        return try {
            val appOps = application.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    application.packageName
                ) == AppOpsManager.MODE_ALLOWED
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    application.packageName
                ) == AppOpsManager.MODE_ALLOWED
            }
        } catch (e: Exception) {
            false
        }
    }

    fun requestUsageStatsPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "ForegroundAppMonitor"
    }
}