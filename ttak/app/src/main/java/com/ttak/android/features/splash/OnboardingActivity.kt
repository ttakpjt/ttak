package com.ttak.android.features.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ttak.android.features.auth.LoginActivity
import com.ttak.android.common.ui.theme.TtakTheme
import com.ttak.android.features.splash.ui.screens.OnboardingScreen

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TtakTheme {
                OnboardingScreen(onComplete = {
                    // 안내 화면 완료 후 MainActivity로 이동
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                })
            }
        }
    }
}