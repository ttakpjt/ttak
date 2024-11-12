package com.ttak.android.domain.model.history

data class WeeklyPickResponse(
    val code: String,
    val message: String,
    val data: WeeklyPickData
)

data class WeeklyWatchingResponse(
    val code: String,
    val message: String,
    val data: WeeklyWatchingData
)

data class WeeklyPickData(
    val myCount: Int
)

data class WeeklyWatchingData(
    val followerNum: Int
)