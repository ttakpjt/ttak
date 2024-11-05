package com.ttak.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "selected_apps")
data class SelectedAppEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val packageName: String,
    val appName: String,
    val iconPath: String
)