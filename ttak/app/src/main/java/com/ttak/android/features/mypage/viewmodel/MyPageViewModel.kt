package com.ttak.android.features.mypage.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.repository.MyPageRepository
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.ttak.android.network.implementation.MyPageApiImpl
import com.ttak.android.network.util.ApiConfig

class NicknameViewModel(application: Application) : AndroidViewModel(application) {
    // MyPageApi를 활용한 새로운 repository
    private val myPageRepository: MyPageRepository = MyPageApiImpl(
        ApiConfig.createMyPageApi(application)
    )
    // checkNickname 메소드: 닉네임 중복 검사
    fun checkNickname(nickname: String, onResult: (Boolean) -> Unit) {
        // 비동기 요청 수행
        viewModelScope.launch {
            val result = myPageRepository.checkNickname(nickname)
            onResult(result.isSuccess && result.getOrNull()?.data == null)  // 중복 체크 결과 전달
        }
    }

    // registerNickname 메소드: 닉네임 등록
    fun registerNickname(nickname: String) {
        viewModelScope.launch {
            val result = myPageRepository.registerNickname(nickname)
            if (result.isSuccess) {
                Log.d("닉네임", "닉네임 등록 성공: ${result.getOrNull()}")
            } else {
                Log.e("닉네임", "닉네임 등록 실패: ${result.exceptionOrNull()}")
            }
        }
    }
}
