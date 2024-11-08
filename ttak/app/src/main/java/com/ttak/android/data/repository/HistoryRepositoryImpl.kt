package com.ttak.android.data.repository

import com.ttak.android.domain.model.HistoryInfo
import com.ttak.android.network.api.HistoryApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryRepositoryImpl(
    private val api: HistoryApi
) : HistoryRepository {

    override suspend fun getWeeklyPickCount(): Int = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getWeeklyPickCount()
            if (response.isSuccessful) {
                response.body()?.takeIf { it.code == "200" }?.data?.myCount
            } else null
        }.getOrElse { 0 } ?: 0
    }

    override suspend fun getMessages(): List<HistoryInfo> = withContext(Dispatchers.IO) {
        try {
            api.getMessages()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun sendMessage(message: String): Unit = withContext(Dispatchers.IO) {
        try {
            val response = api.sendMessage(message)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to send message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}