package com.ttak.android.domain.model

import java.time.LocalDateTime
import java.time.LocalTime

data class FocusGoal(
    val id: Long = 0,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val selectedApps: List<AppInfo>,
    val isEnabled: Boolean = true
)