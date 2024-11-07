package com.ttak.android.features.auth

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.ttak.android.MainActivity
import com.ttak.android.features.auth.ui.screens.SplashScreen
import com.ttak.android.common.ui.theme.TtakTheme
import com.ttak.android.common.monitor.ForegroundAppMonitor

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
//                    hasPermissions = foregroundAppMonitor.hasUsageStatsPermission(),
                    hasPermissions = checkUsageStatsPermission(this),
                    hasOverlayPermission = checkOverlayPermission(),
                    onPermissionsConfirmed = {
                        if (!checkUsageStatsPermission(this)) {
                            isPermissionRequested = true
                            requestUsageStatsPermission(this)
                        }
                    },
//                    onPermissionsConfirmed = {
//                        // 권한 설정이 필요할 경우 실행할 동작
//                        if (!foregroundAppMonitor.hasUsageStatsPermission()) {
//                            isPermissionRequested = true
//                            foregroundAppMonitor.requestUsageStatsPermission(this@SplashActivity)
//                        }
//                    },
                    onOverlayPermissionConfirmed = {
                        // 오버레이 권한 요청
                        if (!checkOverlayPermission()) {
                            isOverlayPermissionRequested = true
                            requestOverlayPermission()
                        }
                    }
                )
            }
        }
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

    override fun onResume() {
        super.onResume()
        // 권한을 요청하고 설정 화면을 나왔을 때 확인
        if (isPermissionRequested) {
            isPermissionRequested = false
            if (foregroundAppMonitor.hasUsageStatsPermission()) {
                navigateToNextScreen()
            } else {
                // 권한 설정을 거부했을 때 앱 종료
                Toast.makeText(this, "권한이 필요합니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show()
                finishAffinity()  // 앱 종료
            }
        }
        // 오버레이 권한 확인
        if (isOverlayPermissionRequested) {
            isOverlayPermissionRequested = false
            if (checkOverlayPermission()) {
                navigateToNextScreen()
            } else {
                Toast.makeText(this, "오버레이 권한이 없어 일부 기능이 제한됩니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 사용 시간 권한 확인
    private fun checkUsageStatsPermission(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    // 사용 시간 권한 요청
    private fun requestUsageStatsPermission(activity: SplashActivity) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        activity.startActivityForResult(intent, REQUEST_USAGE_STATS_PERMISSION)
    }

    // 권한을 얻었으면 화면 이동
    private fun navigateToNextScreen() {
        val nextActivity =
            if (checkLoginStatus()) MainActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, nextActivity))
        finish()
    }

    companion object {
        private const val REQUEST_OVERLAY_PERMISSION = 1001
        private const val REQUEST_USAGE_STATS_PERMISSION = 1002
    }
}