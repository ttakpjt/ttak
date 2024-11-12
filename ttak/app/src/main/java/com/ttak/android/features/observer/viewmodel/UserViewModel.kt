package com.ttak.android.features.observer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ttak.android.domain.model.User
import com.ttak.android.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun searchUsers(query: String) {
        if (query.isBlank()) return  // 빈 검색어일 경우 아무 것도 하지 않음

        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val results = repository.searchUsers(query)
                _searchResults.value = results
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _searchResults.value = emptyList()
                _uiState.value = UiState.Error(e.message ?: "검색 중 오류가 발생했습니다")
            }
        }
    }

    // 검색 결과 초기화 함수 추가
    fun clearSearchResults() {
        _searchResults.value = emptyList()
        _uiState.value = UiState.Idle
    }

    fun addFriend(user: User) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val result = repository.addFriend(user.userId)
                result.fold(
                    onSuccess = {
                        _uiState.value = UiState.Success
                    },
                    onFailure = { error ->
                        _uiState.value = UiState.Error(error.message ?: "친구 추가에 실패했습니다")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "친구 추가 중 오류가 발생했습니다")
            }
        }
    }


    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }
}