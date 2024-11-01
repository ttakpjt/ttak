package com.ttak.android.data.model

data class FriendStory(
    val id: String,
    val name: String,
    val profileImageUrl: String,
    val hasNewStory: Boolean = false
)