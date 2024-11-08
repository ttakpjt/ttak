package com.ttak.android.network.api

import com.ttak.android.domain.model.UserModel
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Body


data class SignInResponse(
    val code: String,
    val message: String,
    val data: UserData,
)

data class UserData(
    val userId: String
)

data class StringData(
    val data: String
)

interface MemberApi {
    // 구글 계정 정보 보내기
    @POST("user/save")
    suspend fun signIn(@Body user: UserModel): Response<SignInResponse>
}