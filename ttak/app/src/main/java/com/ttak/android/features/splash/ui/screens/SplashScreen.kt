package com.ttak.android.features.splash.ui.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.window.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.ttak.android.MainActivity
import com.ttak.android.R
import com.ttak.android.features.auth.LoginActivity
import com.ttak.android.features.splash.SplashActivity
import com.ttak.android.features.splash.OnboardingActivity
import com.ttak.android.features.mypage.ProfileSetupActivity
import com.ttak.android.utils.UserPreferences
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    context: Context,
    isLoggedIn: Boolean,
    hasPermissions: Boolean,
    hasOverlayPermission: Boolean,
    onPermissionsConfirmed: () -> Unit,
    onOverlayPermissionConfirmed: () -> Unit
) {

    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(GifDecoder.Factory())
        }
        .build()

    // 모든 권한이 승인된 후 다음 화면으로 이동
    LaunchedEffect(hasPermissions, hasOverlayPermission) {
        delay(2500)

        if (hasPermissions && hasOverlayPermission) {
            val isProfileSetupComplete =
                UserPreferences(context.applicationContext).getNickname() != null

            val nextActivity = when {

                isLoggedIn && isProfileSetupComplete -> MainActivity::class.java    // 로그인과 프로필 설정 완료
                isLoggedIn && !isProfileSetupComplete -> ProfileSetupActivity::class.java   // 프로필 설정 미완료
                else -> LoginActivity::class.java   // 로그인 미 완료
            }

            context.startActivity(Intent(context, nextActivity))
            (context as? SplashActivity)?.finish()
        } else {
            // 권한 요청이 필요할 경우 콜백 호출
            if (!hasPermissions) {
                onPermissionsConfirmed()
            }
            if (!hasOverlayPermission) {
                onOverlayPermissionConfirmed()
            }

            // 첫 실행 여부 확인
            val userPreferences = UserPreferences(context)
            Log.d("귯", "${userPreferences.isFirstLaunch}")
            if (userPreferences.isFirstLaunch) {
                context.startActivity(Intent(context, OnboardingActivity::class.java))
                (context as? SplashActivity)?.finish()
            }
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
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(R.drawable.ttak_logo)
                        .build(),
                    imageLoader = imageLoader
                ),
                contentDescription = "Ttak 로고",
                modifier = Modifier.size(300.dp)
            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = "King of Anyang",
//                style = MaterialTheme.typography.titleLarge,
//                color = White
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "로딩 중..",
//                style = MaterialTheme.typography.labelSmall,
//                color = Grey
//            )
        }
    }
}