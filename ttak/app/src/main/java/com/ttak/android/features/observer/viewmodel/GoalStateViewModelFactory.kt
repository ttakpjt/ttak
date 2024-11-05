package com.ttak.android.features.observer.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ttak.android.data.repository.FocusGoalRepository

class GoalStateViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalStateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoalStateViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}