package com.ttak.android.network.implementation

import android.content.Context
import android.util.Log
import com.ttak.android.network.api.ObserverApi
import com.ttak.android.network.util.ApiConfig
import com.ttak.android.network.util.UserPreferences
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class ObserverApiImpl private constructor(context: Context) : ObserverApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    private val userPreferences = UserPreferences(context.applicationContext)

    companion object {
        private const val TAG = "ObserverApi"

        @Volatile
        private var instance: ObserverApiImpl? = null

        fun getInstance(context: Context): ObserverApiImpl {
            return instance ?: synchronized(this) {
                instance ?: ObserverApiImpl(context.applicationContext).also { instance = it }
            }
        }
    }

    override suspend fun updateMyStatus(state: Int): Result<Unit> {
        return try {
            val userId = userPreferences.getUserId()

            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}friends/status?state=$state")
                .post(ByteArray(0).toRequestBody())
                .apply {
                    userId?.let {
                        addHeader("user", it.toString())
                        Log.d(TAG, "userId 헤더 추가됨: $it")
                    } ?: Log.d(TAG, "userId가 null임, 헤더 추가 안 됨")
                }
                .build()

            // Request 로깅
            Log.d(TAG, """
            ===== Update Status Request =====
            URL: ${request.url}
            Method: ${request.method}
            Headers: ${request.headers}
        """.trimIndent())

            val response = client.newCall(request).execute()

            // Response 로깅
            Log.d(TAG, """
            ===== Update Status Response =====
            Code: ${response.code}
            Message: ${response.message}
            Headers: ${response.headers}
            Body: ${response.body?.string()}
        """.trimIndent())

            if (response.isSuccessful) {
                Log.d(TAG, "My status update success: $state")
                Result.success(Unit)
            } else {
                Log.e(TAG, """
                My status update failed:
                Code: ${response.code}
                Error Body: ${response.body?.string()}
            """.trimIndent())
                Result.failure(Exception("API failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in status update", e)
            Result.failure(e)
        }
    }
}