package com.ttak.android.features.auth

import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foregroundAppMonitor = ForegroundAppMonitor(application)

        setContent {
            TtakTheme {
                SplashScreen(
                    context = this,
                    isLoggedIn = checkLoginStatus(),
                    hasPermissions = foregroundAppMonitor.hasUsageStatsPermission(),
                    onPermissionsConfirmed = {
                        // 권한 설정이 필요할 경우 실행할 동작
                        if (!foregroundAppMonitor.hasUsageStatsPermission()) {
                            isPermissionRequested = true
                            foregroundAppMonitor.requestUsageStatsPermission(this@SplashActivity)
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
    }

    // 권한을 얻었으면 화면 이동
    private fun navigateToNextScreen() {
        val nextActivity =
            if (checkLoginStatus()) MainActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, nextActivity))
        finish()
    }
}