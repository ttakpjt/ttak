package com.ttak.android.network.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.ttak.android.data.repository.MyPageRepository
import com.ttak.android.domain.model.MyPageResponse
import com.ttak.android.domain.model.NicknameRequest
import com.ttak.android.network.api.MyPageApi

class MyPageApiImpl (
    private val api: MyPageApi
    ) : MyPageRepository {

        // 닉네임 중복 검사
        override suspend fun checkNickname(nickname: String): Result<MyPageResponse> = handleApiResponse {
            val nicknameRequest = NicknameRequest(nickname)
            val response = api.checkNickname(nicknameRequest)
            Log.d("닉네임", "닉네임 중복 검사 결과2: $response $nickname")
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("API 호출 실패"))
            }
        }

        // 닉네임 등록
        override suspend fun registerNickname(nickname: String): Result<MyPageResponse> = handleApiResponse {
            val nicknameRequest = NicknameRequest(nickname)
            val response = api.registerNickname(nicknameRequest)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("API 호출 실패"))
            }
        }

        // 공통 예외 처리
        private suspend fun <T> handleApiResponse(apiCall: suspend () -> Result<T>): Result<T> {
            return withContext(Dispatchers.IO) {
                try {
                    apiCall()
                } catch (e: Exception) {
                    Log.e("API", "API 요청 중 예외 발생", e)
                    Result.failure(e)
                }
            }
        }
    }