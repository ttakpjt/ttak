package com.ttak.android.data.repository

import com.ttak.android.domain.model.MyPageResponse

interface MyPageRepository {
    suspend fun checkNickname(nickname: String): Result<MyPageResponse>
    suspend fun registerNickname(nickname: String): Result<MyPageResponse>
}