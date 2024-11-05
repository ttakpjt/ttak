package com.ttak.android.domain.model

data class FriendStory(
    val id: String,
    val name: String,
    val profileImageUrl: String,
    val hasNewStory: Boolean = false
)