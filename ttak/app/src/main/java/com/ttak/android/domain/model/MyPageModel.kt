package com.ttak.android.domain.model

// 응답
data class MyPageResponse(
    val code: String,
    val message: String,
    val data: String,
)

// 요청
data class NicknameRequest(val nickname: String)