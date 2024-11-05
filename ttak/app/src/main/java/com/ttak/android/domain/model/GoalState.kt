package com.ttak.android.domain.model

data class Time(
    val hour: Int,
    val minute: Int
)

data class GoalState(
    val isSet: Boolean = false,
    val observerCount: Int = 0,
    val startTime: Time = Time(18, 0),  // 기본값 6:00PM
    val endTime: Time = Time(20, 0),    // 기본값 8:00PM
    val currentTime: Time = Time(19, 0)  // 기본값 7:00PM
)