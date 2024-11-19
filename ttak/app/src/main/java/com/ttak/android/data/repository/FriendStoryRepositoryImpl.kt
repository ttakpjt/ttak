package com.ttak.android.data.repository

import com.ttak.android.domain.model.FriendStory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FriendStoryRepositoryImpl : FriendStoryRepository {
    private val friends = MutableStateFlow<List<FriendStory>>(emptyList())

    override fun getAllFriends(): Flow<List<FriendStory>> = friends

    override fun getFriendsWithNewStories(): Flow<List<FriendStory>> =
        friends.map { list -> list.filter { it.hasNewStory } }

    override suspend fun updateFriends(newFriends: List<FriendStory>) {
        friends.emit(newFriends)
    }
}