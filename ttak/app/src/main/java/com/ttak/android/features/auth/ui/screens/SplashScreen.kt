package com.ttak.android.features.auth.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttak.android.R
import kotlinx.coroutines.delay
import com.ttak.android.MainActivity
import com.ttak.android.LoginActivity
import com.ttak.android.SplashActivity

@Composable
fun SplashScreen(
    context: Context,
    isLoggedIn: Boolean
) {
    // 2초 후 다음 화면으로 이동
    LaunchedEffect(Unit) {
        delay(2000)
        val nextActivity = if (isLoggedIn) MainActivity::class.java else LoginActivity::class.java
        context.startActivity(Intent(context, nextActivity))
        (context as? SplashActivity)?.finish()
    }

    // 스플래시 화면 UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E181E)), // 배경색
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.default_proflie),  // 앱 로고 이미지
                contentDescription = "앱 로고",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "앱 이름",  // 앱 이름 텍스트
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "로딩 중...",  // 로딩 텍스트
                fontSize = 16.sp,
                color = Color.LightGray
            )
        }
    }
}