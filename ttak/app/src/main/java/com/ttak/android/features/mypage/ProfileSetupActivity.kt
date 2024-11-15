package com.ttak.android.features.mypage

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.ttak.android.common.ui.theme.TtakTheme
import com.ttak.android.features.mypage.ui.screens.ProfileSetupScreen
import com.ttak.android.features.mypage.viewmodel.NicknameViewModel

class ProfileSetupActivity : ComponentActivity() {
    private val viewModel: NicknameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TtakTheme {
                // Compose 내에서 상태 정의
                val profileImageUri = remember { mutableStateOf<Uri?>(null) }

                // 이미지 선택 런처 정의
                val selectImageLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    profileImageUri.value = uri
                }

                // ProfileSetupScreen 호출
                ProfileSetupScreen(
                    profileImageUri = profileImageUri,
                    selectImageLauncher = selectImageLauncher,
                    viewModel = viewModel
                )
            }
        }
    }
}
