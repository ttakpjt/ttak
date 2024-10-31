package com.ttak.android.features.observer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.model.GoalState
import com.ttak.android.data.model.Time
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalViewModel : ViewModel() {
    private val _goalState = MutableStateFlow(GoalState())
    val goalState: StateFlow<GoalState> = _goalState.asStateFlow()

    // 서버에서 데이터 가져오기
    fun fetchGoalState() {
        viewModelScope.launch {
            // 서버 통신 로직
            // _goalState.value = 서버에서 받아온 데이터
        }
    }

    // 목표 시간 설정하기
    fun setGoal(startTime: Time, endTime: Time) {
        viewModelScope.launch {
            // 서버에 목표 설정 요청
            _goalState.value = _goalState.value.copy(
                isSet = true,
                startTime = startTime,
                endTime = endTime
            )
        }
    }

    // 현재 시간 업데이트
    fun updateCurrentTime(currentTime: Time) {
        viewModelScope.launch {
            _goalState.value = _goalState.value.copy(
                currentTime = currentTime
            )
        }
    }

    // 선택적: 서버와의 통신을 위한 시간 변환 메서드들
    private fun timeToMinutes(time: Time): Int = time.hour * 60 + time.minute

    private fun minutesToTime(minutes: Int): Time = Time(
        hour = minutes / 60,
        minute = minutes % 60
    )
}