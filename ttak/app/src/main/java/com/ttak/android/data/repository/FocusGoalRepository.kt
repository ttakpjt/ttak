package com.ttak.android.data.repository

import com.ttak.android.data.local.dao.FocusGoalDao
import com.ttak.android.data.local.dao.SelectedAppDao
import com.ttak.android.data.local.entity.FocusGoalEntity
import com.ttak.android.data.local.entity.SelectedAppEntity
import com.ttak.android.domain.model.FocusGoal
import kotlinx.coroutines.flow.Flow

class FocusGoalRepository(
    private val focusGoalDao: FocusGoalDao,
    private val selectedAppDao: SelectedAppDao
) {
    suspend fun saveFocusGoal(focusGoal: FocusGoal) {
        val goalEntity = FocusGoalEntity(
            startTimeMillis = focusGoal.startTime.toNanoOfDay() / 1_000_000,
            endTimeMillis = focusGoal.endTime.toNanoOfDay() / 1_000_000,
            isEnabled = focusGoal.isEnabled
        )
        val goalId = focusGoalDao.insert(goalEntity)

        val selectedAppEntities = focusGoal.selectedApps.map { app ->
            SelectedAppEntity(
                goalId = goalId,
                packageName = app.packageName,
                appName = app.appName,
                iconPath = app.iconPath
            )
        }
        selectedAppDao.insertAll(selectedAppEntities)
    }
}