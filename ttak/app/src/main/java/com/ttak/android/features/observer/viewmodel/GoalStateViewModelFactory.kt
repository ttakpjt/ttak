package com.ttak.android.features.observer.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ttak.android.data.repository.FocusGoalRepository
import com.ttak.android.data.repository.history.HistoryRepository

class GoalStateViewModelFactory(
    private val application: Application,
    private val historyRepository: HistoryRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalStateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoalStateViewModel(
                application = application,
                historyRepository = historyRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}