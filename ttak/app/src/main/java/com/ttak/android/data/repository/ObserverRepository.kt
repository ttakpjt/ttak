package com.ttak.android.data.repository

import com.ttak.android.domain.model.CountResponse

interface ObserverRepository {
    suspend fun getPickRank(): Result<CountResponse>
}