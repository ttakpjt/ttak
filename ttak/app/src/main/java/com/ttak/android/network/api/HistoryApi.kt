package com.ttak.android.network.api

import com.ttak.android.domain.model.HistoryInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response

interface HistoryApi {
    
    @GET("messages")
    suspend fun getMessages(): List<HistoryInfo>

    @POST("messages/send")
    suspend fun sendMessage(@Body message: String): Response<Unit>
}