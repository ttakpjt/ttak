package com.ttak.android.domain.model

import java.time.LocalTime

data class FocusGoal(
    val id: Long = 0,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val selectedApps: List<AppInfo>,
    val isEnabled: Boolean = true
)