package com.ttak.android.data.repository

import com.ttak.android.data.model.FriendStory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PreviewFriendStoryRepository : FriendStoryRepository {
    override fun getAllFriends(): Flow<List<FriendStory>> = flow {
        emit(listOf(
            FriendStory("1", "탁싸피", "url1", true),
            FriendStory("2", "황싸피", "url2", false),
            FriendStory("3", "김싸피", "url3", false),
            FriendStory("4", "탁싸피", "url1", false),
            FriendStory("5", "김싸피", "url3", false),
            FriendStory("6", "탁싸피", "url1", false),
            FriendStory("7", "김싸피", "url3", false),
            FriendStory("8", "탁싸피", "url1", false),
            FriendStory("9", "김싸피", "url3", false),
            FriendStory("10", "탁싸피", "url1", false),
            FriendStory("11", "김싸피", "url3", false),
            FriendStory("12", "탁싸피", "url1", false),
            FriendStory("13", "김싸피", "url3", false)
        ))
    }

    override fun getFriendsWithNewStories(): Flow<List<FriendStory>> = flow {
        emit(listOf(
            FriendStory("1", "탁싸피", "url1", true),
            FriendStory("3", "김싸피", "url3", true)

        ))
    }

    override suspend fun updateFriends(newFriends: List<FriendStory>) {
        // Preview에서는 아무 동작도 하지 않음
    }
}