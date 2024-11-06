package com.ttak.android.network.api

interface ObserverApi {
    suspend fun updateMyStatus(state: Int): Result<Unit>
}