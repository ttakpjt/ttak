package com.ttak.android.domain.model

// 응답
data class MyPageResponse(
    val code: String,
    val message: String,
    val data: String,
)

data class PresignUrlResponse(
    val code: String,
    val message: String,
    val data: UrlData
)

data class UrlData(
    val url: String
)

// 요청
data class NicknameRequest(val nickname: String)