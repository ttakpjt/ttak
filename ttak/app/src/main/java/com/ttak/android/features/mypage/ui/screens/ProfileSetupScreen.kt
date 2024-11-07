package com.ttak.android.features.mypage.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttak.android.MainActivity
import com.ttak.android.R
import com.ttak.android.common.ui.components.AppButton
import com.ttak.android.common.ui.components.AppSearchBar
import com.ttak.android.common.ui.theme.Blue
import com.ttak.android.common.ui.theme.Black
import com.ttak.android.common.ui.theme.White
import com.ttak.android.features.mypage.ui.components.ProfileImagePicker

@Composable
fun ProfileSetupScreen(
    profileImageUri: MutableState<Uri?>,
    selectImageLauncher: ActivityResultLauncher<String>
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 프로필 소제목
        Text(
            text = "프로필 설정",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = White,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 프로필 사진 설정 화면
//        ProfileImagePicker(
//            profileImageUri = profileImageUri,
//            selectImageLauncher = selectImageLauncher
//        )

        Spacer(modifier = Modifier.height(24.dp))

        // 닉네임 입력
        AppSearchBar(
            icon = R.drawable.ic_check_circle,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 저장하고 다음 화면으로 넘어가는 버튼
        AppButton(
            text = "저장",
            backgroundColor = Blue,
            contentColor = Black,
            onClick = {
                val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("isProfileSetupComplete", true).apply()

                context.startActivity(Intent(context, MainActivity::class.java))
                (context as? Activity)?.finish()
            }
        )
    }
}
