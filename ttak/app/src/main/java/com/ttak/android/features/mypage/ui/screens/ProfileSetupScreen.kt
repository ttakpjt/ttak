package com.ttak.android.features.mypage.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.ttak.android.features.mypage.viewmodel.NicknameViewModel
import com.ttak.android.network.util.UserPreferences

@Composable
fun ProfileSetupScreen(
    profileImageUri: MutableState<Uri?>,
    selectImageLauncher: ActivityResultLauncher<String>,
    viewModel: NicknameViewModel,
    onNicknameCheck: (Boolean) -> Unit  // 닉네임 중복 여부 결과 콜백
) {
    val context = LocalContext.current
    var nickname by remember { mutableStateOf("") }  // 닉네임 상태 추가
    var isNicknameAvailable by remember { mutableStateOf(false) }  // 닉네임 중복 여부
    var isError by remember { mutableStateOf(false) }  // 오류 상태 추가
    var errorMessage by remember { mutableStateOf("") } // 에러 메시지 상태 추가

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
            style = MaterialTheme.typography.titleLarge,
//            fontSize = 32.sp,
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
            icon = if (isNicknameAvailable) R.drawable.ic_check_circle else R.drawable.check_circle_outline,
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            errorMessage = errorMessage,
            // 닉네임 중복 여부 확인
            onIconClick = { inputNickname ->
                viewModel.checkNickname(inputNickname) { isAvailable, serverMessage  ->
                    isError = !isAvailable  // 닉네임 중복 확인 결과에 따라 경고 문구 표시
                    onNicknameCheck(isAvailable)  // 결과 콜백
                    isNicknameAvailable = isAvailable  // 닉네임 중복 결과 저장
                    if (isAvailable) {
                        nickname = inputNickname  // 사용 가능한 닉네임을 저장
                    } else {
                        errorMessage = serverMessage ?: "이미 존재하는 닉네임입니다."
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 저장하고 다음 화면으로 넘어가는 버튼
        AppButton(
            text = "저장",
            backgroundColor = Blue,
            contentColor = Black,
            onClick = {
                if (isNicknameAvailable) {
                    viewModel.registerNickname(nickname) { isRegistered ->
                        if (isRegistered) {
                            UserPreferences(context.applicationContext).saveNickname(nickname)

                            val sharedPreferences =
                                context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                            sharedPreferences.edit().putBoolean("isProfileSetupComplete", true)
                                .apply()

                            context.startActivity(Intent(context, MainActivity::class.java))
                            (context as? Activity)?.finish()
                        } else {
                            // 등록 실패 시 오류 메시지와 상태 설정
                            isError = true
                            errorMessage = "닉네임 등록에 실패했습니다. 다시 시도해 주세요."
                        }
                    }
                } else {
                    isError = true
                    errorMessage = "닉네임 중복을 확인해 주세요."
                }
            }
        )
    }
}
