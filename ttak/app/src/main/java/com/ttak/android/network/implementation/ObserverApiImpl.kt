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
                .url("${ApiConfig.BASE_URL}/my/status?state=$state")
                .post(ByteArray(0).toRequestBody())  // create() 대신 toRequestBody() 사용
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                Log.d(TAG, "My status update success: $state")
                Result.success(Unit)
            } else {
                Log.e(TAG, "My status update failed: ${response.code}")
                Result.failure(Exception("API failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in status update", e)
            Result.failure(e)
        }
    }
}