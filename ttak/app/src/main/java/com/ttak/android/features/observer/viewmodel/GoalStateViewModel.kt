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
import java.time.LocalDateTime
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

        // 시간 업데이트를 위한 별도의 코루틴
        viewModelScope.launch {
            while (true) {
                delay(60_000) // 1분 대기
                val currentDateTime = LocalDateTime.now()
                val currentState = _goalState.value
                if (currentState.isSet) {
                    // 전체 상태 업데이트
                    repository.getAllGoals().collect { goals ->
                        updateGoalState(goals)
                    }
                }
            }
        }
    }

    private fun updateGoalState(goals: List<FocusGoal>) {
        val currentDateTime = LocalDateTime.now()

        viewModelScope.launch {
            goals.forEach { goal ->
                // 종료 시간이 지난 목표는 비활성화
                if (goal.isEnabled && goal.endDateTime < currentDateTime) {
                    repository.toggleGoalEnabled(goal.id)
                }
            }
        }

        val activeGoal = goals.firstOrNull { goal ->
            goal.isEnabled && isTimeInRange(currentDateTime, goal.startDateTime, goal.endDateTime)
        }

        // 다음 예정된 목표 찾기 (오늘)
        val nextGoal = goals.firstOrNull { goal ->
            goal.isEnabled &&
                    goal.startDateTime > currentDateTime &&
                    goal.startDateTime.toLocalDate() == currentDateTime.toLocalDate()  // 같은 날짜인지 확인
        }

        _goalState.value = when {
            activeGoal != null -> GoalState(
                isSet = true,
                observerCount = 3,
                startTime = Time(activeGoal.startDateTime.hour, activeGoal.startDateTime.minute),
                endTime = Time(activeGoal.endDateTime.hour, activeGoal.endDateTime.minute),
                currentTime = Time(currentDateTime.hour, currentDateTime.minute),
                selectedApps = activeGoal.selectedApps
            )
            nextGoal != null -> GoalState(
                isSet = true,
                observerCount = 0,
                startTime = Time(nextGoal.startDateTime.hour, nextGoal.startDateTime.minute),
                endTime = Time(nextGoal.endDateTime.hour, nextGoal.endDateTime.minute),
                currentTime = Time(nextGoal.startDateTime.hour, nextGoal.startDateTime.minute),
                selectedApps = nextGoal.selectedApps
            )
            else -> GoalState(isSet = false)
        }
    }

    private fun isTimeInRange(current: LocalDateTime, start: LocalDateTime, end: LocalDateTime): Boolean {
        return if (start.isBefore(end)) {
            !current.isBefore(start) && !current.isAfter(end)
        } else {
            // 자정을 걸치는 경우
            !current.isBefore(start) || !current.isAfter(end)
        }
    }
}