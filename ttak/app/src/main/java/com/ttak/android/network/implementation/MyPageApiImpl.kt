package com.ttak.android.network.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.ttak.android.data.repository.MyPageRepository
import com.ttak.android.domain.model.MyPageResponse
import com.ttak.android.domain.model.NicknameRequest
import com.ttak.android.network.api.MyPageApi

class MyPageApiImpl(
    private val api: MyPageApi
) : MyPageRepository {

    // 닉네임 중복 검사
    override suspend fun checkNickname(nickname: String): Result<MyPageResponse> = handleApiResponse {
        val nicknameRequest = NicknameRequest(nickname)

        // Request 로깅
        Log.d("API", "=== Check Nickname Request ===")
        Log.d("API", "Endpoint: /check-nickname")
        Log.d("API", "Request Headers: ${api.checkNickname(nicknameRequest).raw().request.headers}")
        Log.d("API", "Request Body: $nicknameRequest")

        val response = api.checkNickname(nicknameRequest)

        // Response 로깅
        Log.d("API", "=== Check Nickname Response ===")
        Log.d("API", "Response Code: ${response.code()}")
        Log.d("API", "Response Headers: ${response.headers()}")
        Log.d("API", "Response Body: ${response.body()}")

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

        // Request 로깅
        Log.d("API", "=== Register Nickname Request ===")
        Log.d("API", "Endpoint: /register-nickname")
        Log.d("API", "Request Headers: ${api.registerNickname(nicknameRequest).raw().request.headers}")
        Log.d("API", "Request Body: $nicknameRequest")

        val response = api.registerNickname(nicknameRequest)

        // Response 로깅
        Log.d("API", "=== Register Nickname Response ===")
        Log.d("API", "Response Code: ${response.code()}")
        Log.d("API", "Response Headers: ${response.headers()}")
        Log.d("API", "Response Body: ${response.body()}")

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