package com.ttak.android.network.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkModule {
    companion object {
        private const val BASE_URL = "https://k11a509.p.ssafy.io/api/"

        fun provideHistoryApi(): HistoryApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(HistoryApi::class.java)
        }
    }
}