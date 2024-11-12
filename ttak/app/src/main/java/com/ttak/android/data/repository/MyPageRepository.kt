package com.ttak.android.data.repository

import com.ttak.android.domain.model.MyPageResponse
import retrofit2.Response

interface MyPageRepository {
    suspend fun checkNickname(nickname: String): Response<MyPageResponse>
    suspend fun registerNickname(nickname: String): Result<MyPageResponse>
}