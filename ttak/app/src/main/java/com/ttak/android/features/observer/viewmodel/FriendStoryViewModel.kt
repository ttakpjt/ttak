package com.ttak.android.features.observer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.model.FilterOption
import com.ttak.android.data.model.FriendStory
import com.ttak.android.data.repository.FriendStoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FriendStoryViewModel(
    private val repository: FriendStoryRepository
) : ViewModel() {

    private val _selectedFilterId = MutableStateFlow(1)
    val selectedFilterId: StateFlow<Int> = _selectedFilterId.asStateFlow()

    private val _filterOptions = MutableStateFlow<List<FilterOption>>(emptyList())
    val filterOptions: StateFlow<List<FilterOption>> = _filterOptions.asStateFlow()

    private val _friends = MutableStateFlow<List<FriendStory>>(emptyList())
    val friends: StateFlow<List<FriendStory>> = _friends.asStateFlow()

    init {
        viewModelScope.launch {
            // Combine flows to update filter options when friends list changes
            combine(
                repository.getAllFriends(),
                repository.getFriendsWithNewStories()
            ) { allFriends, newStoryFriends ->
                _filterOptions.value = listOf(
                    FilterOption(1, "전체", allFriends.size),
                    FilterOption(2, "감지", newStoryFriends.size)
                )
            }.collect()
        }

        viewModelScope.launch {
            // Update displayed friends based on selected filter
            combine(
                repository.getAllFriends(),
                selectedFilterId
            ) { allFriends, filterId ->
                when (filterId) {
                    1 -> allFriends
                    2 -> allFriends.filter { it.hasNewStory }
                    else -> allFriends
                }
            }.collect {
                _friends.value = it
            }
        }
    }

    fun setSelectedFilter(filterId: Int) {
        _selectedFilterId.value = filterId
    }

    fun loadInitialData() {
        viewModelScope.launch {
            // Sample initial data - in real app, this would come from a data source
            repository.updateFriends(
                listOf(
                    FriendStory("1", "탁싸피", "url1", true),
                    FriendStory("2", "황싸피", "url2", false),
                    // ... more friends
                )
            )
        }
    }
}