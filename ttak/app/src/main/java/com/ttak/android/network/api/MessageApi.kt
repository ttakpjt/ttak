package com.ttak.android.network.api

import com.ttak.android.domain.model.MessageData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MessageApi {
    @POST("fcm/send/message")
    suspend fun sendMessage(@Body data: MessageData): Response<Unit>
}