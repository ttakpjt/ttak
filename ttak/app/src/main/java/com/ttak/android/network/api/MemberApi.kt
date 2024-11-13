package com.ttak.android.network.api

import com.ttak.android.domain.model.MemberResponse

import com.ttak.android.domain.model.SignInResponse
import com.ttak.android.domain.model.MemberRequest
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Body


interface MemberApi {
    // 구글 계정 정보 보내기
    @POST("user/save")
    suspend fun signIn(@Body user: MemberRequest): Response<SignInResponse>

    // 로그아웃
    @GET("user/logout")
    suspend fun logout(): Response<MemberResponse>

    // 닉네임이 존재하는지 확인
    @GET("user/exist/nickname")
    suspend fun existNickname(): Response<MemberResponse>
}