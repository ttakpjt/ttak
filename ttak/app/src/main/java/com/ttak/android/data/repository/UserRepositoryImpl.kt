package com.ttak.android.data.repository

import com.ttak.android.domain.model.User
import com.ttak.android.network.api.UserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import retrofit2.Response

class UserRepositoryImpl(
    private val api: UserApi
) : UserRepository {

    override suspend fun searchUsers(query: String): List<User> = withContext(Dispatchers.IO) {
        try {
            // API 요청 전 로그
            Log.d("UserRepositoryImpl", "Searching users with nickname: $query")

            val response = api.searchUsers(query)
            logApiResponse(response, "User Search")

            when {
                response.isSuccessful -> {
                    val apiResponse = response.body()
                    if (apiResponse?.code == "200" && apiResponse.message == "SUCCESS") {
                        apiResponse.data  // List<User>를 반환
                    } else {
                        Log.e("UserRepositoryImpl", "API 응답 실패: code=${apiResponse?.code}, message=${apiResponse?.message}")
                        emptyList()
                    }
                }
                else -> {
                    Log.e("UserRepositoryImpl", "검색 실패: ${response.code()}")
                    emptyList()
                }
            }
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "검색 중 오류 발생", e)
            emptyList()
        }
    }
    override suspend fun addFriend(userId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("UserRepositoryImpl", "Adding friend with ID: $userId")

            val response = api.addFriend(userId)
            logApiResponse(response, "Add Friend")

            when {
                response.isSuccessful -> Result.success(Unit)
                else -> Result.failure(Exception("친구 추가 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "친구 추가 중 오류 발생", e)
            Result.failure(e)
        }
    }

    // API 응답 로깅을 위한 확장 함수
    private fun <T> logApiResponse(response: Response<T>, apiName: String) {
        if (response.isSuccessful) {
            Log.d("UserRepositoryImpl", """
                $apiName API 성공:
                - URL: ${response.raw().request.url}
                - Method: ${response.raw().request.method}
                - Headers: ${response.raw().request.headers}
                - Response Code: ${response.code()}
                - Response Body: ${response.body()}
            """.trimIndent())
        } else {
            Log.e("UserRepositoryImpl", """
                $apiName API 실패:
                - URL: ${response.raw().request.url}
                - Method: ${response.raw().request.method}
                - Headers: ${response.raw().request.headers}
                - Response Code: ${response.code()}
                - Error Body: ${response.errorBody()?.string()}
            """.trimIndent())
        }
    }
}