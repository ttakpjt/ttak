package com.ttak.android.data.repository

import com.ttak.android.domain.model.User

interface UserRepository {
    suspend fun searchUsers(query: String): List<User>
    suspend fun addFriend(followingId: Long): Result<Unit>
}