package com.ttak.android.network.api

import com.ttak.android.domain.model.MyPageResponse
import com.ttak.android.domain.model.NicknameRequest
import com.ttak.android.domain.model.PresignUrlResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Url


interface MyPageApi {
    // 닉네임 중복 확인
    @POST("user/check/nickname")
    suspend fun checkNickname(@Body nickname: NicknameRequest): Response<MyPageResponse>

    // 닉네임 등록
    @POST("user/register/nickname")
    suspend fun registerNickname(@Body nickname: NicknameRequest): Response<MyPageResponse>

    // 프로필 사진 등록을 위한 URL 받아오기
    // 우선은 profile로 설정(확장 시 변경 가능)
    @GET("user/s3")
    suspend fun getPresignedUrl(
        @Query("imageName") imageName: String,
        @Query("prefix") prefix: String = "profile"
    ): Response<PresignUrlResponse>

    //프로필 사진 등록
    @PUT
    suspend fun registerProfileImage(
        @Url url: String,  // presigned URL
        @Body image: RequestBody  // 이미지 바이너리 데이터
    ): Response<Unit>
}