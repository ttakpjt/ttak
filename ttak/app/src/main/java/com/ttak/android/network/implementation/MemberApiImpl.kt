package com.ttak.android.network.implementation

import com.ttak.android.domain.model.MemberRequest
import com.ttak.android.network.api.MemberApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.ttak.android.data.repository.MemberRepository
import com.ttak.android.domain.model.MemberResponse
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class MemberApiImpl(
    private val api: MemberApi
) : MemberRepository {

    override suspend fun signIn(user: MemberRequest): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.signIn(user)
            if (response.isSuccessful) {
                // 백엔드에서 전달한 userId 값을 추출하여 반환
                val userId = response.body()?.data?.userId
                if (userId != null) {
                    Result.success(userId)  // userId를 성공적으로 반환
                } else {
                    // userId가 null인 경우 처리
                    Result.failure(Exception("userId가 응답에 포함되지 않았습니다"))
                }
            } else {
                // 실패 응답의 HTTP 상태 코드와 오류 메시지를 로그에 출력
                Log.e("귯", "로그인 실패: 코드=${response.code()}, 메시지=${response.message()}")
                Result.failure(Exception("로그인 실패: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("귯", "API 호출 중 예외 발생", e)
            Result.failure(Exception("API 호출 중 예외 발생: ${e.message}"))
        }
    }

    override suspend fun logout(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.logout()
            if (response.isSuccessful) {
                Result.success("로그아웃 성공")
            } else {
                Result.failure(Exception("로그아웃 실패: ${response.message()}"))
            }
        } catch (
            e: Exception
        ) {
            Result.failure(Exception("로그아웃 중 예외 발생: ${e.message}"))
        }
    }

    // 닉네임이 존재하는지 확인
    override suspend fun existNickname(): Response<MemberResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.existNickname()
            if (response.isSuccessful) {
                response // 응답 그대로 반환
            } else {
                Response.error(
                    response.code(),
                    response.errorBody() ?: "DB에서 닉네임 확인 실패".toResponseBody()
                )
            }
        } catch (e: Exception) {
            // 예외 발생 시 빈 바디와 에러 메시지를 포함한 Response 반환
            Response.error(500, "닉네임 중복 확인 중 예외 발생: ${e.message}".toResponseBody())
        }
    }
}