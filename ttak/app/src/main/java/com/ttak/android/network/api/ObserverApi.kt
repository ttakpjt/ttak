package com.ttak.android.network.api

import com.ttak.android.domain.model.CountResponse
import retrofit2.Response
import retrofit2.http.GET

interface ObserverApi {
    suspend fun updateMyStatus(state: Int): Result<Unit>
}