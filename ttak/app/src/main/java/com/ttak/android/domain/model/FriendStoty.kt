package com.ttak.android.domain.model

data class FriendStory(
    val friendId: Int,
    val friendName: String,
    val friendImg: String,
    val status: Int
) {
    val hasNewStory: Boolean
        get() = status == 1
}