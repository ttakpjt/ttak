package com.ttak.android.domain.model

data class MessageData (
    val userId: Long, // 공격받는 사람 Id
    val data: String // 메시지 내용
)