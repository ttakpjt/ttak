package com.ttak.android.network.implementation

import android.util.Log
import com.ttak.android.network.api.ObserverApi
import com.ttak.android.network.util.ApiConfig
import com.ttak.android.network.util.HttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ObserverApiImpl private constructor() : ObserverApi {
    private val client = HttpClient.client

    companion object {
        private const val TAG = "ObserverApi"

        @Volatile
        private var instance: ObserverApiImpl? = null

        fun getInstance(): ObserverApiImpl {
            return instance ?: synchronized(this) {
                instance ?: ObserverApiImpl().also { instance = it }
            }
        }
    }

    override suspend fun updateMyStatus(state: Int): Result<Unit> {
        return try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}my/status?state=$state")
                .post(ByteArray(0).toRequestBody())
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