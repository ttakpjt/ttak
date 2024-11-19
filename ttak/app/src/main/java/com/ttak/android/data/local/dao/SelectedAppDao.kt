package com.ttak.android.data.local.dao

import androidx.room.*
import com.ttak.android.data.local.entity.SelectedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SelectedAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apps: List<SelectedAppEntity>)

    @Query("SELECT * FROM selected_apps WHERE goalId = :goalId")
    suspend fun getAppsByGoalId(goalId: Long): List<SelectedAppEntity>

    @Query("DELETE FROM selected_apps WHERE goalId = :goalId")
    suspend fun deleteByGoalId(goalId: Long)
}