package com.ttak.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ttak.android.features.auth.ui.screens.SplashScreen

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isLoggedIn = checkLoginStatus()  // 로그인 상태 확인
        setContent {
            SplashScreen(context = this, isLoggedIn = isLoggedIn)
        }
    }

    private fun checkLoginStatus(): Boolean {
        // 로그인 상태 확인 로직
        return false  // 예를 들어 기본값으로 false 반환
    }
}