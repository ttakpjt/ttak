package com.ttak.android.features.observer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.local.AppDatabase
import com.ttak.android.data.repository.FocusGoalRepository
import com.ttak.android.domain.model.FocusGoal
import com.ttak.android.domain.model.GoalState
import com.ttak.android.domain.model.Time
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

class GoalStateViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = FocusGoalRepository(
        focusGoalDao = database.focusGoalDao(),
        selectedAppDao = database.selectedAppDao(),
        packageManager = application.packageManager
    )

    private val _goalState = MutableStateFlow<GoalState>(GoalState())
    val goalState = _goalState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllGoals().collect { goals ->
                updateGoalState(goals)
            }
        }
    }

    private fun updateGoalState(goals: List<FocusGoal>) {
        val currentTime = LocalTime.now()

        // 활성화된 목표 중 현재 시간에 해당하는 목표 찾기
        val activeGoal = goals.firstOrNull { goal ->
            goal.isEnabled && isTimeInRange(currentTime, goal.startTime, goal.endTime)
        }

        // 다음 예정된 목표 찾기 (오늘 또는 내일)
        val nextGoal = goals.firstOrNull { goal ->
            goal.isEnabled && goal.startTime > currentTime
        }

        _goalState.value = when {
            activeGoal != null -> GoalState(
                isSet = true,
                observerCount = 3, // TODO: 실제 관찰자 수 구현
                startTime = Time(activeGoal.startTime.hour, activeGoal.startTime.minute),
                endTime = Time(activeGoal.endTime.hour, activeGoal.endTime.minute),
                currentTime = Time(currentTime.hour, currentTime.minute),
                selectedApps = activeGoal.selectedApps  // 선택된 앱 목록 추가
            )
            nextGoal != null -> GoalState(
                isSet = true,
                observerCount = 0,
                startTime = Time(nextGoal.startTime.hour, nextGoal.startTime.minute),
                endTime = Time(nextGoal.endTime.hour, nextGoal.endTime.minute),
                currentTime = Time(nextGoal.startTime.hour, nextGoal.startTime.minute),
                selectedApps = nextGoal.selectedApps  // 선택된 앱 목록 추가
            )
            else -> GoalState(isSet = false)
        }

        // 1분마다 현재 시간 업데이트
        viewModelScope.launch {
            while (true) {
                delay(60_000) // 1분 대기
                val now = LocalTime.now()
                val currentState = _goalState.value
                if (currentState.isSet) {
                    _goalState.value = currentState.copy(
                        currentTime = Time(now.hour, now.minute)
                    )
                }
            }
        }
    }

    private fun isTimeInRange(current: LocalTime, start: LocalTime, end: LocalTime): Boolean {
        return if (start <= end) {
            current in start..end
        } else {
            // 자정을 걸치는 경우 (예: 오후 11시 ~ 오전 6시)
            current >= start || current <= end
        }
    }
}