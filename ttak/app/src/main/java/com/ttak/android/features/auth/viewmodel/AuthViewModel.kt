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
    private val testRepository: MemberRepository =
        MemberApiImpl(ApiConfig.createTestApi(application))  // createTestApi를 사용한 새로운 repository 추가

    // signIn 메소드: 로그인 처리 후 test 호출
    fun signIn(user: UserModel) {
        // 비동기 요청 수행
        viewModelScope.launch {
            try {
                val result = memberRepository.signIn(user)
                Log.d("이규석", "$result")
                result.let { result ->
                    Log.d("이규석", "로그인 결과: $result")
                    if (result.isSuccess) {
                        // 로그인 성공
                        val userId = result.getOrNull() // userId 값을 가져옴
                        Log.d("이규석", "로그인 성공: $userId")
                        UserPreferences(getApplication()).saveUserId(userId.toString()) // userId를 저장
                        Log.d("이규석", "${UserPreferences(getApplication()).getUserId()}")

                        // 로그인 완료 후 test() 호출
                        test()
                    } else {
                        // 로그인 실패 처리
                        Log.e("이규석", "로그인 실패: ${result.exceptionOrNull()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("이규석", "로그인 실패: $e")
            }
        }
    }

    // test() 메소드: 테스트 요청
    fun test() {
        // 비동기 요청 수행
        viewModelScope.launch {
            try {
                val result = testRepository.test()  // testRepository로 test API 호출
                result.let { result ->
                    Log.d("이규석", "테스트 결과: $result")
                    if (result.isSuccess) {
                        // 테스트 성공
                        val userId = result.getOrNull() // userId 값을 가져옴
                        Log.d("이규석", "테스트 성공: $userId")
                    } else {
                        // 테스트 실패 처리
                        Log.e("이규석", "테스트 실패: ${result.exceptionOrNull()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("이규석", "테스트 실패: $e")
            }
        }
    }
}
