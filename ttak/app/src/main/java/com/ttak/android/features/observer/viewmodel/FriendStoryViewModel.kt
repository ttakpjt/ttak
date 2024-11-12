package com.ttak.android.features.observer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ttak.android.domain.model.FilterOption
import com.ttak.android.domain.model.FriendStory
import com.ttak.android.data.repository.FriendStoryRepository
import com.ttak.android.domain.model.FriendStatusUpdate
import com.ttak.android.network.socket.SocketEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FriendStoryViewModel(
    private val repository: FriendStoryRepository
) : ViewModel() {
    private val TAG = "FriendStoryViewModel"
    private val gson = Gson()

    private val _selectedFilterId = MutableStateFlow(1)
    val selectedFilterId: StateFlow<Int> = _selectedFilterId.asStateFlow()

    private val _filterOptions = MutableStateFlow<List<FilterOption>>(emptyList())
    val filterOptions: StateFlow<List<FilterOption>> = _filterOptions.asStateFlow()

    private val _friends = MutableStateFlow<List<FriendStory>>(emptyList())
    val friends: StateFlow<List<FriendStory>> = _friends.asStateFlow()

    init {
        setupFilterOptionsFlow()
        setupFriendsFlow()
    }

    private fun setupFilterOptionsFlow() {
        viewModelScope.launch {
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
    }

    private fun setupFriendsFlow() {
        viewModelScope.launch {
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

    fun handleWebSocketMessage(event: SocketEvent.MessageReceived) {
        viewModelScope.launch {
            try {
                val statusUpdate = gson.fromJson(event.data, FriendStatusUpdate::class.java)
                Log.d(TAG, "Received status update for user ${statusUpdate.userId}: ${statusUpdate.status}")

                // 현재 친구 목록 가져오기
                val currentFriends = _friends.value.toMutableList()

                // 업데이트할 친구 찾기 - friendId를 사용
                val friendIndex = currentFriends.indexOfFirst { it.friendId == statusUpdate.userId }

                if (friendIndex != -1) {
                    // 친구를 찾았다면 상태 업데이트
                    val updatedFriend = currentFriends[friendIndex].copy(
                        status = statusUpdate.status  // hasNewStory 대신 status를 직접 업데이트
                    )
                    currentFriends[friendIndex] = updatedFriend

                    // 저장소 업데이트
                    repository.updateFriends(currentFriends)
                    Log.d(TAG, "Updated friend ${updatedFriend.friendName} status to ${updatedFriend.status}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing WebSocket message", e)
            }
        }
    }

    fun setSelectedFilter(filterId: Int) {
        _selectedFilterId.value = filterId
    }
}