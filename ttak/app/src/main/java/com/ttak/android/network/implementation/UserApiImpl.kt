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

    // 기존 addFriend 메서드가 있다면 그대로 유지
//    override suspend fun addFriend(userId: Long): Result<Unit> = withContext(Dispatchers.IO) {
//        try {
//            // addFriend API 구현
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Log.e("UserApiImpl", "친구 추가 중 예외 발생", e)
//            Result.failure(e)
//        }
//    }
}