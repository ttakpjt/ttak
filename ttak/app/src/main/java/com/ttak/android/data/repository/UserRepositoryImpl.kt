// data/repository/UserRepositoryImpl.kt
package com.ttak.android.data.repository

import com.ttak.android.domain.model.User
import com.ttak.android.features.observer.domain.repository.UserRepository
import com.ttak.android.network.api.UserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val api: UserApi
) : UserRepository {

    override suspend fun searchUsers(query: String): List<User> = withContext(Dispatchers.IO) {
        try {
            api.searchUsers(query)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addFriend(userId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = api.addFriend(userId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to add friend"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}