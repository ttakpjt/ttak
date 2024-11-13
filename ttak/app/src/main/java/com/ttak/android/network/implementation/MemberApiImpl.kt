package com.ttak.android.network.implementation

import com.ttak.android.domain.model.MemberRequest
import com.ttak.android.network.api.MemberApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.ttak.android.data.repository.auth.MemberRepository
import com.ttak.android.domain.model.MemberResponse
import com.ttak.android.domain.model.SignInResponse
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class MemberApiImpl(
    private val api: MemberApi
) : MemberRepository {

    // 구글 계정 연동
    override suspend fun signIn(user: MemberRequest): Response<SignInResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.signIn(user)
            if (response.isSuccessful) {
                response // 응답 그대로 반환
            } else {
                Response.error(
                    response.code(),
                    response.errorBody() ?: "구글 계정 연동 실패".toResponseBody()
                )
            }
        } catch (e: Exception) {
            Response.error(500, "구글 계정 연동 중 예외 발생: ${e.message}".toResponseBody())
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