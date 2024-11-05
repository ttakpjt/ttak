package com.ttak.android.data.local.dao

import androidx.room.*
import com.ttak.android.data.local.entity.SelectedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SelectedAppDao {
    @Query("SELECT * FROM selected_apps WHERE goalId = :goalId")
    fun getSelectedApps(goalId: Long): Flow<List<SelectedAppEntity>>

    @Insert
    suspend fun insertAll(apps: List<SelectedAppEntity>)
}