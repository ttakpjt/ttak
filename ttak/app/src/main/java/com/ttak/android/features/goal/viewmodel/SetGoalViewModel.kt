package com.ttak.android.features.goal.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.local.AppDatabase
import com.ttak.android.data.repository.FocusGoalRepository
import com.ttak.android.domain.model.AppInfo
import com.ttak.android.domain.model.FocusGoal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

class SetGoalViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = FocusGoalRepository(
        focusGoalDao = database.focusGoalDao(),
        selectedAppDao = database.selectedAppDao()
    )

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
            val packageManager = getApplication<Application>().packageManager
            val apps = packageManager.getInstalledApplications(0)
                .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                .map { appInfo ->
                    AppInfo(
                        packageName = appInfo.packageName,
                        appName = packageManager.getApplicationLabel(appInfo).toString(),
                        iconPath = "" // 아이콘은 drawable resource로 저장
                    )
                }
            _installedApps.value = apps
        }
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
            val focusGoal = FocusGoal(
                startTime = startTime.value,
                endTime = endTime.value,
                selectedApps = installedApps.value.filter {
                    selectedApps.value.contains(it.packageName)
                }
            )
            repository.saveFocusGoal(focusGoal)
        }
    }
}