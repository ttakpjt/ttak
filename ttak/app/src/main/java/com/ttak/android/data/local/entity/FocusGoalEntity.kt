package com.ttak.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_goals")
data class FocusGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startDateTimeMillis: Long,
    val endDateTimeMillis: Long,
    val isEnabled: Boolean
)