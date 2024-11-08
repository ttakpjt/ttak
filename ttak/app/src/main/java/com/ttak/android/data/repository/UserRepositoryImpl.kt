package com.ttak.android.data.repository

import com.ttak.android.domain.model.User
import com.ttak.android.network.api.UserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class UserRepositoryImpl(
    private val api: UserApi
) : UserRepository {

    override suspend fun searchUsers(query: String): List<User> = withContext(Dispatchers.IO) {
        try {
            Log.d("UserRepositoryImpl", """
                API Request Details:
                - Endpoint: GET /user/search
                - Query Parameter: nickname=$query
                - Headers: ${api.searchUsers(query).raw().request.headers}
                - Full URL: ${api.searchUsers(query).raw().request.url}
            """.trimIndent())

            val response = api.searchUsers(query)
            if (response.isSuccessful) {
                Log.d("UserRepositoryImpl", """
                    Search successful:
                    - Status Code: ${response.code()}
                    - Response Body: ${response.body()}
                    - Response Headers: ${response.headers()}
                """.trimIndent())
                response.body() ?: emptyList()
            } else {
                Log.e("UserRepositoryImpl", """
                    Search failed:
                    - Status Code: ${response.code()}
                    - Error Body: ${response.errorBody()?.string()}
                    - Response Headers: ${response.headers()}
                """.trimIndent())
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Search error", e)
            emptyList()
        }
    }

//    override suspend fun addFriend(userId: Long): Result<Boolean> = withContext(Dispatchers.IO) {
//        try {
//            val response = api.addFriend(userId)  // API 메서드도 Long 타입으로 수정 필요
//            if (response.isSuccessful) {
//                Result.success(true)
//            } else {
//                Log.e("UserRepositoryImpl", "친구 추가 실패: 코드=${response.code()}, 메시지=${response.message()}")
//                Result.failure(Exception("친구 추가에 실패했습니다"))
//            }
//        } catch (e: Exception) {
//            Log.e("UserRepositoryImpl", "친구 추가 중 오류 발생", e)
//            Result.failure(e)
//        }
//    }
}