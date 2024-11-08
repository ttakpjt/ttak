package com.ttak.android.features.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.repository.HistoryRepository
import com.ttak.android.domain.model.HistoryInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {

    private val _weeklyPickCount = MutableStateFlow(0)
    val weeklyPickCount: StateFlow<Int> = _weeklyPickCount.asStateFlow()

    private val _messages = MutableStateFlow<List<HistoryInfo>>(emptyList())
    val messages: StateFlow<List<HistoryInfo>> = _messages.asStateFlow()

    private val _systemNotification = MutableStateFlow<String>("")
    val systemNotification: StateFlow<String> = _systemNotification.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val count = repository.getWeeklyPickCount()
                _weeklyPickCount.value = count
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    private suspend fun loadMessages() {
        try {
            val messageList = repository.getMessages()
            _messages.value = messageList
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            try {
                repository.sendMessage(content)
                loadMessages() // Reload messages after sending
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}