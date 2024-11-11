package com.ttak.android.features.mypage.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.repository.MyPageRepository
import kotlinx.coroutines.launch
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.ttak.android.network.implementation.MyPageApiImpl
import com.ttak.android.network.util.ApiConfig

class NicknameViewModel(application: Application) : AndroidViewModel(application) {
    // MyPageApi를 활용한 새로운 repository
    private val myPageRepository: MyPageRepository = MyPageApiImpl(
        ApiConfig.createMyPageApi(application)
    )
    // checkNickname 메소드: 닉네임 중복 검사
    fun checkNickname(nickname: String, onResult: (Boolean, String?) -> Unit) {
        // 비동기 요청 수행
        viewModelScope.launch {
            val result = myPageRepository.checkNickname(nickname)
            val isAvailable = result.isSuccess && result.getOrNull()?.data == null
            Log.d("귯", "$${result.getOrNull()}$")
            val serverMessage = result.getOrNull()?.message ?: "오류 발생" // 메시지 값이 없을 경우 기본 메시지 설정

            onResult(isAvailable, serverMessage) // 콜백에 중복 여부와 메시지 전달
        }
    }

    // registerNickname 메소드: 닉네임 등록
    fun registerNickname(nickname: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = myPageRepository.registerNickname(nickname)
            if (result.isSuccess) {
                onResult(true) // 성공: 등록 성공
            } else {
                Log.e("귯", "닉네임 등록 실패: ${result.exceptionOrNull()}")
                onResult(false)
            }
        }
    }
}
