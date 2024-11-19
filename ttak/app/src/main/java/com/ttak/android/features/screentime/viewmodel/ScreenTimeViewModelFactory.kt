package com.ttak.android.features.screentime.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ScreenTimeViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScreenTimeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScreenTimeViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}