package com.ttak.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_goals")
data class FocusGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val isEnabled: Boolean
)