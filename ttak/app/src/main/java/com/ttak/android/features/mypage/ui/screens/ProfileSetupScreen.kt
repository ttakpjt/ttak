package com.ttak.android.features.mypage.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttak.android.MainActivity
import com.ttak.android.R
import com.ttak.android.common.ui.components.AppButton
import com.ttak.android.common.ui.components.AppSearchBar
import com.ttak.android.common.ui.theme.Grey

@Composable
fun ProfileSetupScreen(
    profileImageUri: MutableState<Uri?>,
    selectImageLauncher: ActivityResultLauncher<String>
) {
    val context = LocalContext.current // Context 가져오기

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 프로필 소제목
        Text(
            text = "프로필 설정",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 프로필 이미지를 선택하는 부분
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.size(120.dp)
        ) {
            Image(
                painter = if (profileImageUri.value != null) {
                    painterResource(id = R.drawable.default_proflie)
                } else {
                    painterResource(R.drawable.ic_launcher_foreground)
                },
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Grey)
            )
            IconButton(
                onClick = { selectImageLauncher.launch("image/*") },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check_circle),
                    contentDescription = "프로필 이미지 선택",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 닉네임을 입력하고 중복 검사하는 부분
        AppSearchBar(
            icon = R.drawable.ic_check_circle,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppButton(
            text = "저장",
            onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as? Activity)?.finish()
            }
        )
    }
}
