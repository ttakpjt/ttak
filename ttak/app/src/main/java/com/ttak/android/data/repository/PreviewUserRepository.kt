package com.ttak.android.data.repository

import com.ttak.android.data.model.User
import com.ttak.android.features.observer.domain.repository.UserRepository

class PreviewUserRepository : UserRepository {
    override suspend fun searchUsers(query: String): List<User> {
        // 미리 정의된 더미 데이터 반환
        return listOf(
            User(
                id = "1",
                name = "ssafy",
                profileImageUrl = "https://picsum.photos/40"
            ),
            User(
                id = "2",
                name = "kimssafy",
                profileImageUrl = "https://picsum.photos/40"
            )
        ).filter { it.name.contains(query, ignoreCase = true) }
    }

    override suspend fun addFriend(userId: String): Result<Boolean> {
        // 항상 성공 반환
        return Result.success(true)
    }
}