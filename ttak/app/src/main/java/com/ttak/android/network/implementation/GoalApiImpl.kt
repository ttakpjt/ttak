package com.ttak.android.network.implementation

import android.util.Log
import com.google.gson.Gson
import com.ttak.android.network.api.GoalApi
import com.ttak.android.network.api.ApplicationSettingRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.runCatching

class GoalApiImpl(private val api: GoalApi) {
    private val gson = Gson()
    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    suspend fun saveApplicationSetting(
        appNames: List<String>,
        startTime: LocalTime,
        endTime: LocalTime
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val request = ApplicationSettingRequest(
                appName = appNames,
                startTime = startTime.format(timeFormatter),
                endTime = endTime.format(timeFormatter)
            )

            val response = api.saveApplicationSetting(request)

            // Request 정보 로깅
            Log.d("API_LOG", """
                =====Request=====
                Headers: ${response.raw().request.headers}
                Body: ${gson.toJson(request)}
                
                =====Response=====
                Code: ${response.code()}
                Headers: ${response.headers()}
                Error Body: ${response.errorBody()?.string()}
            """.trimIndent())

            if (response.isSuccessful) {
                Log.d("API_LOG", "Success!")
                Unit
            } else {
                throw Exception("Failed: ${response.code()}")
            }
        }.onFailure { e ->
            Log.e("API_LOG", "Error: ${e.message}")
        }
    }
}