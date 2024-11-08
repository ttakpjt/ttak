package com.ttak.android.features.auth.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.repository.MemberRepository
import com.ttak.android.domain.model.UserModel
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
    fun signIn(user: UserModel) {
        // 비동기 요청 수행
        viewModelScope.launch {
            try {
                val result = memberRepository.signIn(user)
                result.let { result ->
                    if (result.isSuccess) {
                        // 로그인 성공
                        val userId = result.getOrNull() // userId 값을 가져옴
                        Log.d("귯", "로그인 성공: $result")

                        UserPreferences(getApplication()).saveUserId(userId.toString()) // userId를 저장
                        Log.d("귯", "앞으로 사용할 header: $userId")
                    } else {
                        // 로그인 실패 처리
                        Log.e("귯", "로그인 실패: ${result.exceptionOrNull()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("이규석", "로그인 실패: $e")
            }
        }
    }
}
