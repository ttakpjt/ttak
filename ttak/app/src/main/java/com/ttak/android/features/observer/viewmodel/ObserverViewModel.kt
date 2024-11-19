package com.ttak.android.features.observer.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.repository.ObserverRepository
import com.ttak.android.domain.model.CountResponse
import com.ttak.android.network.implementation.ObserverApiImpl2
import com.ttak.android.network.util.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ObserverViewModel(application: Application) : AndroidViewModel(application) {
    // ObserverAPI를 활용한 새로운 repository
    private val observerRepository: ObserverRepository = ObserverApiImpl2(
        ApiConfig.createObserverApi(application)
    )
    
    // 데이터를 성공적으로 가져왔을 때 값을 담을 변수
    private val _countData = MutableStateFlow<CountResponse?>(null)
    val countData: StateFlow<CountResponse?> = _countData

    // getPickRank 메소드: 주간 딱 걸림 횟수 반환
    fun getPickRank() {
        // 비동기 요청 수행
        viewModelScope.launch {
            val result = observerRepository.getPickRank()
            result.onSuccess { data ->
                _countData.value = data
            }.onFailure {
                Log.e("귯", "주간 딱걸림 횟수 가져오기 실패")
                // 오류 로그 추가 가능
            }
        }
    }
}