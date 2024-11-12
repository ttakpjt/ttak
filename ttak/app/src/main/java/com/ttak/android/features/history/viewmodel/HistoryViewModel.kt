package com.ttak.android.features.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.repository.history.HistoryRepository
import com.ttak.android.domain.model.history.HistoryInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {

    private val _weeklyPickCount = MutableStateFlow(0)
    val weeklyPickCount: StateFlow<Int> = _weeklyPickCount.asStateFlow()

    private val _weeklyWatchingCount = MutableStateFlow(0)
    val weeklyWatchingCount: StateFlow<Int> = _weeklyWatchingCount.asStateFlow()

    private val _historyList = MutableStateFlow<List<HistoryInfo>>(emptyList())
    val historyList: StateFlow<List<HistoryInfo>> = _historyList.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val pickCount = repository.getWeeklyPickCount()
                _weeklyPickCount.value = pickCount
                val watchingCount = repository.getWeeklyWatchingCount()
                _weeklyWatchingCount.value = watchingCount
                val historyList = repository.getHistoryList()
                _historyList.value = historyList
            } catch (e: Exception) {

            }
        }
    }
}