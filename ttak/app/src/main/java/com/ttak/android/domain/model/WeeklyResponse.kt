package com.ttak.android.domain.model

data class WeeklyPickResponse(
    val code: String,
    val message: String,
    val data: WeeklyPickData
)

data class WeeklyPickData(
    val myCount: Int,
    val followerNum: Int
)