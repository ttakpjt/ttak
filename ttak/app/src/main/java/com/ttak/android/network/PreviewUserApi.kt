package com.ttak.android.network

import com.ttak.android.domain.model.User
import retrofit2.Response

class PreviewUserApi : UserApi {
    override suspend fun searchUsers(query: String): List<User> {
        return listOf(
            User("1", "탁싸피", ""),
            User("2", "황싸피", ""),
            User("3", "김싸피", ""),
            User("4", "이싸피", "")
        ).filter { it.name.contains(query, ignoreCase = true) }
    }

    override suspend fun addFriend(userId: String): Response<Unit> {
        return Response.success(Unit)
    }
}