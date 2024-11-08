package com.ttak.android.features.goal.viewmodel

import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.local.AppDatabase
import com.ttak.android.data.repository.FocusGoalRepository
import com.ttak.android.domain.model.AppInfo
import com.ttak.android.domain.model.FocusGoal
import com.ttak.android.network.implementation.GoalApiImpl
import com.ttak.android.network.util.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class SetGoalViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = FocusGoalRepository(
        focusGoalDao = database.focusGoalDao(),
        selectedAppDao = database.selectedAppDao(),
        packageManager = application.packageManager
    )
    private val goalApi = ApiConfig.createGoalApi(application)
    private val goalApiImpl = GoalApiImpl(goalApi)

    private val _startTime = MutableStateFlow(LocalTime.of(9, 0))
    val startTime = _startTime.asStateFlow()

    private val _endTime = MutableStateFlow(LocalTime.of(18, 0))
    val endTime = _endTime.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps = _installedApps.asStateFlow()

    private val _selectedApps = MutableStateFlow<Set<String>>(emptySet())
    val selectedApps = _selectedApps.asStateFlow()

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            try {
                val usageStatsManager = getApplication<Application>().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                val packageManager = getApplication<Application>().packageManager

                // 사용 통계 데이터 가져오기 (30일)
                val currentTime = System.currentTimeMillis()
                val startTime = currentTime - 30L * 24 * 60 * 60 * 1000
                val usageStats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_MONTHLY,
                    startTime,
                    currentTime
                ).associateBy { it.packageName }

                // LAUNCHER 카테고리로 실행 가능한 앱 목록 가져오기
                val mainIntent = Intent(Intent.ACTION_MAIN, null)
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

                val installedApps = packageManager.queryIntentActivities(mainIntent, 0)
                    .map { resolveInfo ->
                        val applicationInfo = resolveInfo.activityInfo.applicationInfo
                        AppInfo(
                            packageName = applicationInfo.packageName,
                            appName = packageManager.getApplicationLabel(applicationInfo).toString(),
                            icon = applicationInfo.loadIcon(packageManager)
                        )
                    }
                    .sortedByDescending { app ->
                        usageStats[app.packageName]?.totalTimeInForeground ?: 0
                    }

                _installedApps.value = installedApps

            } catch (e: SecurityException) {
                openUsageAccessSettings()
            }
        }
    }

    private fun openUsageAccessSettings() {
        val intent = Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(intent)
    }

    fun updateStartTime(hour: Int, minute: Int) {
        _startTime.value = LocalTime.of(hour, minute)
    }

    fun updateEndTime(hour: Int, minute: Int) {
        _endTime.value = LocalTime.of(hour, minute)
    }

    fun toggleAppSelection(packageName: String) {
        val currentSelection = _selectedApps.value.toMutableSet()
        if (currentSelection.contains(packageName)) {
            currentSelection.remove(packageName)
        } else {
            currentSelection.add(packageName)
        }
        _selectedApps.value = currentSelection
    }

    fun saveGoal() {
        viewModelScope.launch {
            runCatching {
                // 기존의 로컬 저장 로직
                val today = LocalDate.now()
                val startDateTime = LocalDateTime.of(today, startTime.value)
                val endDateTime = LocalDateTime.of(today, endTime.value)
                val adjustedEndDateTime = if (endDateTime.isBefore(startDateTime)) {
                    endDateTime.plusDays(1)
                } else {
                    endDateTime
                }

                val selectedAppsList = installedApps.value.filter {
                    selectedApps.value.contains(it.packageName)
                }

                // 로컬 저장
                val focusGoal = FocusGoal(
                    startDateTime = startDateTime,
                    endDateTime = adjustedEndDateTime,
                    selectedApps = selectedAppsList
                )
                repository.saveFocusGoal(focusGoal)

                // 서버 저장 - LocalTime 직접 전달
                val appNames = selectedAppsList.map { it.appName }

                goalApiImpl.saveApplicationSetting(
                    appNames = appNames,
                    startTime = startTime.value,  // LocalTime 직접 전달
                    endTime = endTime.value       // LocalTime 직접 전달
                )
            }.onFailure { e ->
                Log.e("SetGoalViewModel", "Error saving goal", e)
            }
        }
    }
}