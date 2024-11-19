package com.ttak.android.network.util

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object HttpClient {
    val client by lazy {
        OkHttpClient.Builder()
            .readTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }
}