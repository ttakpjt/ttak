package com.ttak.android.features.auth.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.repository.auth.MemberRepository
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

    // signIn 메소드: 구글 계정을 연동하고 반환 값을 UserPreference에 저장
    fun signIn(userModel: MemberRequest, onResult: (Boolean) -> Unit) {
        // 비동기 요청 수행
        viewModelScope.launch {
            try {
                val response = memberRepository.signIn(userModel)
                if (response.isSuccessful) {
                    // 백엔드에서 전달한 userId 값을 추출하여 반환
                    val userId = response.body()?.data?.userId
                    if (userId != null) {
                        // UserPreferences에 userId 저장
                        UserPreferences(getApplication()).saveUserId(userId.toString())
                        onResult(true)  // 로그인 성공 처리
                    } else {
                        // userId가 응답에 없을 경우 실패 처리
                        Log.e("귯", "로그인 실패: userId가 응답에 포함되지 않았습니다.")
                        onResult(false)
                    }
                } else {
                    // 실패한 경우 HTTP 상태 코드 및 오류 메시지를 로그에 출력
                    Log.e("귯", "로그인이 실패했습니다.: 코드=${response.code()}, 메시지=${response.message()}")
                    onResult(false)
                }
            } catch (e: Exception) {
                // 예외 발생 시 로그 출력 및 실패 처리
                Log.e("귯", "로그인 시도 중 오류가 발생했습니다.: $e")
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
    fun existNickname(onNicknameChecked: (String?) -> Unit) { // nullable String 반환
        viewModelScope.launch {
            val response = memberRepository.existNickname().body()
            // 닉네임 존재 여부 확인 후 전달
            response?.let {
                val nickname = it.data
                // 닉네임이 빈 문자열일 경우 null로 처리
                if (nickname.isEmpty()) {
                    onNicknameChecked(null)
                } else {
                    onNicknameChecked(nickname)
                }
            } ?: onNicknameChecked(null) // response가 null인 경우 null 반환
        }
    }
}