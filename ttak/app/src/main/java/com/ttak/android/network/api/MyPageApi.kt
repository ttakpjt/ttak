package com.ttak.android.network.api

import com.ttak.android.domain.model.MyPageResponse
import com.ttak.android.domain.model.NicknameRequest
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body


interface MyPageApi {
    // 닉네임 중복 확인
    @POST("user/check/nickname")
    suspend fun checkNickname(@Body nickname: NicknameRequest): Response<MyPageResponse>

    // 닉네임 등록
    @POST("user/register/nickname")
    suspend fun registerNickname(@Body nickname: NicknameRequest): Response<MyPageResponse>
}