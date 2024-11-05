package com.ttak.android.data.repository

import android.content.pm.PackageManager
import android.util.Log
import com.ttak.android.data.local.dao.FocusGoalDao
import com.ttak.android.data.local.dao.SelectedAppDao
import com.ttak.android.data.local.entity.FocusGoalEntity
import com.ttak.android.data.local.entity.SelectedAppEntity
import com.ttak.android.domain.model.AppInfo
import com.ttak.android.domain.model.FocusGoal
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

class FocusGoalRepository(
    private val focusGoalDao: FocusGoalDao,
    private val selectedAppDao: SelectedAppDao
) {
    // FocusGoalRepository.kt에서
    suspend fun saveFocusGoal(focusGoal: FocusGoal) {
        val goalEntity = FocusGoalEntity(
            startTimeMillis = focusGoal.startTime.toNanoOfDay() / 1_000_000,
            endTimeMillis = focusGoal.endTime.toNanoOfDay() / 1_000_000,
            isEnabled = true
        )
        val goalId = focusGoalDao.insert(goalEntity)

        // 저장된 goalId 로그
        Log.d("FocusGoalRepository", "Saved goal with ID: $goalId")

        val selectedAppEntities = focusGoal.selectedApps.map { app ->
            SelectedAppEntity(
                goalId = goalId,
                packageName = app.packageName,
                appName = app.appName
            )
        }
        selectedAppDao.insertAll(selectedAppEntities)

        // 저장된 앱 목록 로그
        Log.d("FocusGoalRepository", "Saved apps: ${selectedAppEntities.map { it.appName }}")

        // 저장 후 바로 조회해서 확인
        val savedGoal = focusGoalDao.getGoalById(goalId)
        val savedApps = selectedAppDao.getAppsByGoalId(goalId)
        Log.d("FocusGoalRepository", "Verification - Saved goal: $savedGoal")
        Log.d("FocusGoalRepository", "Verification - Saved apps: $savedApps")
    }

    // 데이터를 조회할 때는 패키지매니저를 통해 아이콘을 로드
    suspend fun getFocusGoal(goalId: Long, packageManager: PackageManager): FocusGoal? {
        val goalEntity = focusGoalDao.getGoalById(goalId) ?: return null
        val selectedApps = selectedAppDao.getAppsByGoalId(goalId).map { appEntity ->
            try {
                val applicationInfo = packageManager.getApplicationInfo(appEntity.packageName, 0)
                AppInfo(
                    packageName = appEntity.packageName,
                    appName = appEntity.appName,
                    icon = applicationInfo.loadIcon(packageManager)
                )
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }.filterNotNull()

        return FocusGoal(
            startTime = LocalTime.ofNanoOfDay(goalEntity.startTimeMillis * 1_000_000),
            endTime = LocalTime.ofNanoOfDay(goalEntity.endTimeMillis * 1_000_000),
            isEnabled = goalEntity.isEnabled,
            selectedApps = selectedApps
        )
    }
}