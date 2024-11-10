package com.ttak.android.network.implementation

import com.ttak.android.data.repository.ObserverRepository
import com.ttak.android.domain.model.CountResponse
import com.ttak.android.network.api.ObserverApi2

class ObserverApiImpl2 (
    private val api: ObserverApi2
) : ObserverRepository {

    // 주간 딱걸림 횟수를 가져오는 함수
    override suspend fun getPickRank(): Result<CountResponse> {
        val response = api.pickRank()

        return if (response.isSuccessful) {
            response.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("body가 비어 있습니다."))    // 명시적인 return 필요
        } else {
            Result.failure(Exception("API 호출 실패"))
        }
    }
}