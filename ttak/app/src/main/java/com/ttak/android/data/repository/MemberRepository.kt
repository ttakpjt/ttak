package com.ttak.android.data.repository

import com.ttak.android.domain.model.UserModel

interface   MemberRepository {
    suspend fun signIn(user: UserModel): Result<String>
    suspend fun test(): Result<String>  // test() 메서드 추가
}