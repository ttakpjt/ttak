package com.ttak.android.data.repository

import com.ttak.android.domain.model.MyPageResponse
import com.ttak.android.domain.model.PresignUrlResponse
import okhttp3.RequestBody
import retrofit2.Response

interface MyPageRepository {
    suspend fun checkNickname(nickname: String): Response<MyPageResponse>
    suspend fun registerNickname(nickname: String): Result<MyPageResponse>
    suspend fun getPresignedImage(imageName: String): Response<PresignUrlResponse>
    suspend fun registerProfileImage(url: String, image: RequestBody): Response<Unit>
}