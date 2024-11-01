package com.ttak.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.ttak.android.features.auth.ui.screens.SplashScreen
import com.ttak.android.common.ui.theme.TtakTheme

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isLoggedIn = checkLoginStatus()  // 로그인 상태 확인
        setContent {
            TtakTheme {
                SplashScreen(context = this, isLoggedIn = isLoggedIn)
            }
        }
    }

    private fun checkLoginStatus(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}