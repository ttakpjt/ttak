package com.ttak.android.domain.model

data class FriendStatus(
    val id: String,
    val status: Int  // 1: 초기상태(false), -1: 토글(true)
)