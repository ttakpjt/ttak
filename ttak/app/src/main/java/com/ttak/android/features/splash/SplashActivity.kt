package com.ttak.android.features.splash

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.ttak.android.MainActivity
import com.ttak.android.common.monitor.ForegroundAppMonitor
import com.ttak.android.common.ui.theme.TtakTheme
import com.ttak.android.features.auth.LoginActivity
import com.ttak.android.features.splash.ui.screens.SplashScreen

class SplashActivity : ComponentActivity() {
    private lateinit var foregroundAppMonitor: ForegroundAppMonitor
    private var isPermissionRequested = false   // 권한 설정 요청 여부를 확인하는 변수
    private var isOverlayPermissionRequested = false // 오버레이 권한 요청 여부 확인

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        foregroundAppMonitor = ForegroundAppMonitor(application)

        setContent {
            TtakTheme {
                SplashScreen(
                    context = this,
                    isLoggedIn = checkLoginStatus(),
                    hasPermissions = checkUsageStatsPermission(this),
                    hasOverlayPermission = checkOverlayPermission(),
                    onPermissionsConfirmed = { requestUsageStatsPermission(this) },
                    onOverlayPermissionConfirmed = { requestOverlayPermission() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionsAndNavigate()
    }

    // 권한 확인 및 화면 이동 제어
    private fun checkPermissionsAndNavigate() {
        val hasUsageStatsPermission = checkUsageStatsPermission(this)
        val hasOverlayPermission = checkOverlayPermission()

        if (isPermissionRequested && hasUsageStatsPermission) {
            isPermissionRequested = false
        }

        if (isOverlayPermissionRequested && hasOverlayPermission) {
            isOverlayPermissionRequested = false
        }

        if (hasUsageStatsPermission && hasOverlayPermission) {
            navigateToNextScreen()
        } else {
            if (!hasUsageStatsPermission && !isPermissionRequested) {
                isPermissionRequested = true
                requestUsageStatsPermission(this)
            }

            if (!hasOverlayPermission && !isOverlayPermissionRequested) {
                isOverlayPermissionRequested = true
                requestOverlayPermission()
            }
        }
    }

    // 권한을 얻었으면 화면 이동
    fun navigateToNextScreen() {
        val nextActivity =
            if (checkLoginStatus()) MainActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, nextActivity))
        finish()
    }

    // 로그인 상태 확인
    private fun checkLoginStatus(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    // 오버레이 권환 확인
    private fun checkOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    // 오버레이 권한 요청
    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
    }


    // 사용 시간 권한 확인
    private fun checkUsageStatsPermission(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    // 사용 시간 권한 요청
    private fun requestUsageStatsPermission(activity: SplashActivity) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        activity.startActivityForResult(intent, REQUEST_USAGE_STATS_PERMISSION)
    }

    companion object {
        private const val REQUEST_OVERLAY_PERMISSION = 1001
        private const val REQUEST_USAGE_STATS_PERMISSION = 1002
    }
}