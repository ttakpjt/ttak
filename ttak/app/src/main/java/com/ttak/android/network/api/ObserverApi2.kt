package com.ttak.android.network.api

import com.ttak.android.domain.model.CountResponse
import retrofit2.Response
import retrofit2.http.GET

interface ObserverApi2 {

    // 랭킹용 주간 딱걸림
    @GET("history/pick-rank")
    suspend fun pickRank(): Response<CountResponse>
}