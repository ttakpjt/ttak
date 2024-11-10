package com.ttak.android.domain.model

data class CountResponseData(
    val totalCount: Int,
    val myCount: Int,
    val friendsCount: Int
)

// 응답
data class CountResponse(
    val code: String,
    val message: String,
    val data: CountResponseData,
)