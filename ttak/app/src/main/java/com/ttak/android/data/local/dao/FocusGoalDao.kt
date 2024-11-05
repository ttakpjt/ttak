package com.ttak.android.data.local.dao

import androidx.room.*
import com.ttak.android.data.local.entity.FocusGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusGoalDao {
    @Query("SELECT * FROM focus_goals")
    fun getFocusGoals(): Flow<List<FocusGoalEntity>>

    @Insert
    suspend fun insert(focusGoal: FocusGoalEntity): Long

    @Update
    suspend fun update(focusGoal: FocusGoalEntity)
}