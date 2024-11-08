package com.ttak.android.domain.model.response

import com.ttak.android.domain.model.FriendStory

data class FriendResponse(
    val message: String,
    val data: List<FriendStory>
)
