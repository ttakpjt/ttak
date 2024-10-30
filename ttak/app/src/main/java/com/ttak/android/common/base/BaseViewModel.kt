package com.ttak.android.common.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    // 로딩 상태를 나타내는 LiveData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // 에러 메시지를 나타내는 LiveData, null 허용 설정
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // 로딩 상태 업데이트
    protected fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    // 에러 메시지 설정
    protected fun setError(message: String?) {
        _errorMessage.value = message
    }

    // 에러 메시지 초기화 (null로 설정 가능)
    fun clearError() {
        _errorMessage.value = null
    }
}