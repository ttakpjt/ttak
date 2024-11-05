package com.ttak.android.features.observer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ttak.android.data.repository.FriendStoryRepository

class FriendStoryViewModelFactory(
    private val repository: FriendStoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendStoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendStoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}