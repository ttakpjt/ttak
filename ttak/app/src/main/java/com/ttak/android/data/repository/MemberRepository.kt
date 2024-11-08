package com.ttak.android.data.repository

import com.ttak.android.domain.model.UserModel

interface MemberRepository {
    suspend fun signIn(user: UserModel): Result<String>
}