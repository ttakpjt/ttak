package com.ttak.android.network.api

import com.ttak.android.domain.model.history.HistoryListResponse
import com.ttak.android.domain.model.history.WeeklyPickResponse
import com.ttak.android.domain.model.history.WeeklyWatchingResponse
import retrofit2.Response
import retrofit2.http.GET

interface HistoryApi {

    @GET("history/pick")
    suspend fun getWeeklyPickCount(): Response<WeeklyPickResponse>

    @GET("friends/follower")
    suspend fun getWeeklyWatchingCount(): Response<WeeklyWatchingResponse>

    @GET("history/list")
    suspend fun getHistoryList(): Response<HistoryListResponse>
}