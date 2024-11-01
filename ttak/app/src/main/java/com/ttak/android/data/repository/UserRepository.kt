package com.ttak.android.features.observer.domain.repository

import com.ttak.android.data.model.User

interface UserRepository {
    suspend fun searchUsers(query: String): List<User>
    suspend fun addFriend(userId: String): Result<Boolean>
}