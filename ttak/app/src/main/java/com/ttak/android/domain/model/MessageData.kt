package com.ttak.android.domain.model

data class NotificationData(
    val title: String,
    val body: String
)

data class MessageData (
    val userId: String, // 공격받는 사람 Id
    val data: NotificationData
)