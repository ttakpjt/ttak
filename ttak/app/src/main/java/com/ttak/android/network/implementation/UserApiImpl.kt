package com.ttak.android.network.implementation

import com.ttak.android.domain.model.User
import com.ttak.android.network.api.UserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.ttak.android.data.repository.UserRepository

class UserApiImpl(
    private val api: UserApi
) : UserRepository {
    override suspend fun searchUsers(query: String): List<User> = withContext(Dispatchers.IO) {
        try {
            Log.d("UserApiImpl", "Searching users with query: $query")
            val response = api.searchUsers(query)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("UserApiImpl", "검색 실패: 코드=${response.code()}, 메시지=${response.message()}")
                throw Exception("사용자 검색 실패: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("UserApiImpl", "API 호출 중 예외 발생", e)
            throw Exception("사용자 검색 중 오류 발생: ${e.message}")
        }
    }

    override suspend fun addFriend(followingId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("UserApiImpl", "Adding friend with ID: $followingId")
            val response = api.addFriend(followingId)
            if (response.isSuccessful) {
                Log.d("UserApiImpl", "친구 추가 성공")
                Result.success(Unit)
            } else {
                Log.e("UserApiImpl", "친구 추가 실패: 코드=${response.code()}, 메시지=${response.message()}")
                Result.failure(Exception("친구 추가 실패: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("UserApiImpl", "친구 추가 중 예외 발생", e)
            Result.failure(e)
        }
    }
}