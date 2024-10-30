package com.ttak.android.data.repository

import com.ttak.android.data.model.UserModel

interface UserRepository {

    // 사용자 정보를 가져오는 메서드
    suspend fun getUserById(userId: String): UserModel?

    // 사용자 정보를 저장하는 메서드
    suspend fun saveUser(user: UserModel)

    // 사용자를 삭제하는 메서드
    suspend fun deleteUser(userId: String)
}