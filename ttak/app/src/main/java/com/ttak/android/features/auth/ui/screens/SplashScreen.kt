package com.ttak.android.features.auth.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.ttak.android.common.ui.theme.Grey
import com.ttak.android.common.ui.theme.White
import com.ttak.android.features.auth.LoginActivity
import com.ttak.android.features.auth.SplashActivity
import com.ttak.android.features.mypage.ProfileSetupActivity

@Composable
fun SplashScreen(
    context: Context,
    isLoggedIn: Boolean,
    hasPermissions: Boolean,
    onPermissionsConfirmed: () -> Unit
) {
    // 모든 권한이 승인된 후 다음 화면으로 이동
    LaunchedEffect(hasPermissions) {
        delay(1000)

        if (hasPermissions) {
            val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            val isProfileSetupComplete = sharedPreferences.getBoolean("isProfileSetupComplete", false)

            val nextActivity = when {
                isLoggedIn && isProfileSetupComplete -> MainActivity::class.java    // 로그인과 프로필 설정 완료
                isLoggedIn && !isProfileSetupComplete -> ProfileSetupActivity::class.java   // 프로필 설정 미완료
                else -> LoginActivity::class.java   // 로그인 미 완료
            }

            context.startActivity(Intent(context, nextActivity))
            (context as? SplashActivity)?.finish()
        } else {
            // 권한 요청이 필요할 경우 콜백 호출
            onPermissionsConfirmed()
        }
    }

    // 스플래시 화면 UI
    Box(
        modifier = Modifier
            .fillMaxSize(),
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
                text = "King of Anyang",
                style = MaterialTheme.typography.titleLarge,
                color = White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "로딩 중..",
                style = MaterialTheme.typography.labelSmall,
                color = Grey
            )
        }
    }
}