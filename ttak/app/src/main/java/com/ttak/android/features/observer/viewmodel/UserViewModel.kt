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
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading

                // 빈 쿼리 처리
                if (query.isBlank()) {
                    _searchResults.value = emptyList()
                    _uiState.value = UiState.Success
                    return@launch
                }

                // 최소 검색어 길이 체크
                if (query.length < 2) {
                    _searchResults.value = emptyList()
                    _uiState.value = UiState.Success
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    repository.searchUsers(query)
                }.let { results ->
                    _searchResults.value = results
                    _uiState.value = UiState.Success
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun addFriend(user: User) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading

                val result = withContext(Dispatchers.IO) {
                    repository.addFriend(user.id)
                }

                result.fold(
                    onSuccess = {
                        _uiState.value = UiState.Success
                    },
                    onFailure = { error ->
                        _uiState.value = UiState.Error(error.message ?: "Failed to add friend")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to add friend")
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