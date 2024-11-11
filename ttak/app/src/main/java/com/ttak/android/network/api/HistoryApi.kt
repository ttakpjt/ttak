package com.ttak.android.network.api

import com.ttak.android.domain.model.HistoryInfo
import com.ttak.android.domain.model.WeeklyPickResponse
import com.ttak.android.domain.model.WeeklyWatchingResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response

interface HistoryApi {

    @GET("history/pick")
    suspend fun getWeeklyPickCount(): Response<WeeklyPickResponse>

    @GET("friends/follower")
    suspend fun getWeeklyWatchingCount(): Response<WeeklyWatchingResponse>
    
    @GET("messages")
    suspend fun getMessages(): List<HistoryInfo>

    @POST("messages/send")
    suspend fun sendMessage(@Body message: String): Response<Unit>
}