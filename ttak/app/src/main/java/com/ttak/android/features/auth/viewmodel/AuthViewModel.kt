package com.ttak.android.features.auth.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.repository.MemberRepository
import com.ttak.android.domain.model.MemberRequest
import kotlinx.coroutines.launch
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.ttak.android.network.implementation.MemberApiImpl
import com.ttak.android.network.util.ApiConfig
import com.ttak.android.network.util.UserPreferences

class MemberViewModel(application: Application) : AndroidViewModel(application) {
    private val memberRepository: MemberRepository =
        MemberApiImpl(ApiConfig.createMemberApi(application))

    // signIn 메소드: 로그인 처리 후 test 호출
    fun signIn(user: MemberRequest, onResult: (Boolean) -> Unit) {
        // 비동기 요청 수행
        viewModelScope.launch {
            try {
                val result = memberRepository.signIn(user)
                result.let { result ->
                    if (result.isSuccess) {
                        // 로그인 성공
                        val userId = result.getOrNull() // userId 값을 가져옴

                        UserPreferences(getApplication()).saveUserId(userId.toString()) // userId를 저장
                        onResult(true)
                    } else {
                        // 로그인 실패 처리
                        Log.e("귯", "로그인 실패: ${result.exceptionOrNull()}")
                        onResult(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("귯", "로그인 실패: $e")
                onResult(false)
            }
        }
    }

    // 로그아웃
    fun logout() {
        viewModelScope.launch {
            val result = memberRepository.logout()
            result.let { result ->
                if (result.isSuccess) {
                    // 로그아웃 성공
                    Log.d("귯", "로그아웃 성공")
                } else {
                    // 로그아웃 실패 처리
                    Log.e("귯", "로그아웃 실패: ${result.exceptionOrNull()}")
                }
            }
        }
    }

    // 서버에 닉네임이 존재하는지 확인
    fun existNickname(onNicknameChecked: (String) -> Unit) {
        viewModelScope.launch {
            val response = memberRepository.existNickname().body()
            // 닉네임 전달
            response?.let {
                onNicknameChecked(it.data)
            }
        }
    }
}