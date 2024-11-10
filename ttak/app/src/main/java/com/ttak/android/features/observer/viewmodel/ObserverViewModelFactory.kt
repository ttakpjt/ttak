package com.ttak.android.features.observer.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ObserverViewModelFactory(
    private val application: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ObserverViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ObserverViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}