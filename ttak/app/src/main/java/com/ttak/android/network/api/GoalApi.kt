package com.ttak.android.network.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class ApplicationSettingRequest(
    val appName: List<String>,
    val startTime: String,
    val endTime: String
)

interface GoalApi {
    @POST("/application/setting")
    suspend fun saveApplicationSetting(@Body request: ApplicationSettingRequest): Response<Unit>
}