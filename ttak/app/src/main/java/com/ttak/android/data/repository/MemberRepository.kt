package com.ttak.android.data.repository

import com.ttak.android.domain.model.MemberRequest
import com.ttak.android.domain.model.MemberResponse
import retrofit2.Response


interface MemberRepository {
    suspend fun signIn(user: MemberRequest): Result<String>
    suspend fun logout(): Result<String>
    suspend fun existNickname(): Response<MemberResponse>
}