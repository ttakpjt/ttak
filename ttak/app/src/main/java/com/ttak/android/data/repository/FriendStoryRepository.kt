package com.ttak.android.data.repository

import com.ttak.android.data.model.FriendStory
import kotlinx.coroutines.flow.Flow

interface FriendStoryRepository {
    fun getAllFriends(): Flow<List<FriendStory>>
    fun getFriendsWithNewStories(): Flow<List<FriendStory>>
    suspend fun updateFriends(newFriends: List<FriendStory>)
}