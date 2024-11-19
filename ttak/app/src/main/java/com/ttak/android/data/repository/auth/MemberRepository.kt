package com.ttak.android.data.repository.auth

import com.ttak.android.domain.model.MemberRequest
import com.ttak.android.domain.model.MemberResponse
import com.ttak.android.domain.model.SignInResponse
import retrofit2.Response


interface MemberRepository {
    suspend fun signIn(user: MemberRequest): Response<SignInResponse>
    suspend fun logout(): Result<String>
    suspend fun existNickname(): Response<MemberResponse>
}