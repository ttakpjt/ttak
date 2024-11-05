package com.ttak.android.data.local.dao

import androidx.room.*
import com.ttak.android.data.local.entity.FocusGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(focusGoal: FocusGoalEntity): Long

    @Query("SELECT * FROM focus_goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: Long): FocusGoalEntity?

    @Query("SELECT * FROM focus_goals")
    fun getAllGoals(): Flow<List<FocusGoalEntity>>
}