package com.ttak.android.features.mypage.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.common.io.Files.getFileExtension
import com.ttak.android.MainActivity
import com.ttak.android.R
import com.ttak.android.common.ui.components.AppButton
import com.ttak.android.common.ui.components.AppSearchBar
import com.ttak.android.common.ui.theme.Blue
import com.ttak.android.common.ui.theme.Black
import com.ttak.android.features.mypage.ui.components.ProfileImagePicker
import com.ttak.android.features.mypage.viewmodel.NicknameViewModel
import com.ttak.android.features.splash.OnboardingActivity
import com.ttak.android.utils.UserPreferences
import com.ttak.android.utils.getFileNameFromUri
import com.ttak.android.utils.getMimeTypeFromExtension
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun ProfileSetupScreen(
    profileImageUri: MutableState<Uri?>,
    selectImageLauncher: ActivityResultLauncher<String>,
    viewModel: NicknameViewModel,
) {
    val context = LocalContext.current
    var nickname by remember { mutableStateOf("") }
    var isNicknameAvailable by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    fun setError(message: String) {
        isError = true
        errorMessage = message
    }

    fun navigateToNextScreen() {
        val prefs = UserPreferences(context.applicationContext)
        if (prefs.isFirstLaunch) {
            prefs.isFirstLaunch = false
            context.startActivity(Intent(context, OnboardingActivity::class.java))
        } else {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
        (context as? Activity)?.finish()
    }

    fun uploadImageAndRegisterNickname(imageUri: Uri, nickname: String) {
        isLoading = true
        val contentResolver = context.contentResolver
        // 파일 이름 추출
        val fileName = getFileNameFromUri(contentResolver, imageUri)

        // 파일 확장자 추출
        val extension = fileName.substringAfterLast('.', "")

        // MIME 타입 설정
        val mimeType = getMimeTypeFromExtension(extension)
        viewModel.getPresignedUrl(fileName) { presignedUrlResponse ->
            if (!presignedUrlResponse.isSuccessful) {
                setError("사진 등록 중 오류가 발생하였습니다.")
                isLoading = false
                return@getPresignedUrl
            }

            val presignedUrl = presignedUrlResponse.body()?.data?.url ?: ""
            val inputStream = contentResolver.openInputStream(imageUri)
            val imageBytes = inputStream?.readBytes() ?: byteArrayOf()
            val requestBody = imageBytes.toRequestBody(mimeType.toMediaTypeOrNull())

            viewModel.registerProfileImage(presignedUrl, requestBody) { uploadResponse ->
                if (!uploadResponse.isSuccessful) {
                    setError("이미지 파일을 등록하지 못 하였습니다.")
                    isLoading = false
                    return@registerProfileImage
                }

                viewModel.registerNickname(nickname) { isRegistered ->
                    if (isRegistered) {
                        UserPreferences(context.applicationContext).saveNickname(nickname)
                        navigateToNextScreen()
                    } else {
                        setError("닉네임 등록에 실패했습니다. 다시 시도해 주세요.")
                        isLoading = false
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "프로필 설정",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        ProfileImagePicker(
            profileImageUri = profileImageUri,
            selectImageLauncher = selectImageLauncher
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppSearchBar(
            icon = if (isNicknameAvailable) R.drawable.ic_check_circle else R.drawable.check_circle_outline,
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            errorMessage = errorMessage,
            onIconClick = { inputNickname ->
                viewModel.checkNickname(inputNickname) { isAvailable, serverMessage ->
                    isError = !isAvailable
                    isNicknameAvailable = isAvailable
                    if (isAvailable) {
                        nickname = inputNickname
                    } else {
                        setError(serverMessage ?: "닉네임 중복을 확인할 수 없습니다.")
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(48.dp))

        AppButton(
            text = if (isLoading) "" else "저장",
            backgroundColor = if (isLoading) Color.Transparent else Blue,
            contentColor = Black,
            onClick = {
                if (!isNicknameAvailable) {
                    setError("닉네임 중복을 확인해 주세요.")
                } else if (profileImageUri.value == null) {
                    setError("사진을 등록해 주세요.")
                } else {
                    uploadImageAndRegisterNickname(profileImageUri.value!!, nickname)
                }
            }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}