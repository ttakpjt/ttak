//package com.ttak.android.network.implementation
//
//import android.util.Log
//import com.ttak.android.data.repository.FriendStoryRepository
//import com.ttak.android.domain.model.FriendStory
//import com.ttak.android.network.api.FriendApi
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.withContext
//
//class FriendApiImpl(private val api: FriendApi) : FriendStoryRepository {
//    private val friends = MutableStateFlow<List<FriendStory>>(emptyList())
//
//    override fun getAllFriends(): Flow<List<FriendStory>> = friends
//
//    override fun getFriendsWithNewStories(): Flow<List<FriendStory>> =
//        friends.map { list -> list.filter { it.hasNewStory } }
//
//    override suspend fun updateFriends(newFriends: List<FriendStory>) {
//        friends.emit(newFriends)
//    }
//
//    suspend fun fetchFriends() = withContext(Dispatchers.IO) {
//        try {
//            Log.d("API", "=== Get Friends List Request ===")
//            Log.d("API", "Endpoint: friends/live")
//
//            val response = api.getFriendsList()
//
//            // 응답 전체를 로그로 확인
//            Log.d("API", "Raw Response: ${response.raw()}")
//            Log.d("API", "Response Code: ${response.code()}")
//            if (!response.isSuccessful) {
//                Log.e("API", "Error Body: ${response.errorBody()?.string()}")
//            }
//            Log.d("API", "Response Body: ${response.body()}")
//
//            if (response.isSuccessful) {
//                response.body()?.let { apiResponse ->
//                    updateFriends(apiResponse.data)
//                    Result.success(apiResponse.data)
//                } ?: Result.failure(Exception("Response body is null"))
//            } else {
//                Result.failure(Exception("API 호출 실패: ${response.code()} - ${response.errorBody()?.string()}"))
//            }
//        } catch (e: Exception) {
//            Log.e("API", "API 요청 중 예외 발생", e)
//            Result.failure(e)
//        }
//    }
//}

package com.ttak.android.network.implementation

import android.util.Log
import com.ttak.android.data.repository.FriendStoryRepository
import com.ttak.android.domain.model.FriendStory
import com.ttak.android.network.api.FriendApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FriendApiImpl(private val api: FriendApi) : FriendStoryRepository {
    private val friends = MutableStateFlow<List<FriendStory>>(emptyList())

    override fun getAllFriends(): Flow<List<FriendStory>> = friends

    override fun getFriendsWithNewStories(): Flow<List<FriendStory>> =
        friends.map { list -> list.filter { it.hasNewStory } }

    override suspend fun updateFriends(newFriends: List<FriendStory>) {
        friends.emit(newFriends)
    }

    suspend fun fetchFriends() = withContext(Dispatchers.IO) {
        try {
            Log.d("API", "=== Get Friends List Request ===")
            Log.d("API", "Endpoint: friends/live")

            val response = api.getFriendsList()

            Log.d("API", "=== Get Friends List Response ===")
            Log.d("API", "Response Code: ${response.code()}")
            Log.d("API", "Response Body: ${response.body()}")

            if (!response.isSuccessful) {
                Log.e("API", "Error Body: ${response.errorBody()?.string()}")
            }

            if (response.isSuccessful) {
                response.body()?.let { friendResponse ->
                    if (friendResponse.message == "SUCCESS") {
                        val friendsList = friendResponse.data
                        updateFriends(friendsList)
                        Result.success(friendsList)
                    } else {
                        Result.failure(Exception("API 응답 실패: ${friendResponse.message}"))
                    }
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("API 호출 실패: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Log.e("API", "API 요청 중 예외 발생", e)
            Result.failure(e)
        }
    }
}