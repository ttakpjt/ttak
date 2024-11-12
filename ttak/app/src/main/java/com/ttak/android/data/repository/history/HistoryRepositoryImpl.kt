package com.ttak.android.data.repository.history

import com.ttak.android.domain.model.history.HistoryInfo
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

    override suspend fun getWeeklyWatchingCount(): Int = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getWeeklyWatchingCount()
            if (response.isSuccessful) {
                response.body()?.takeIf { it.code == "200" }?.data?.followerNum
            } else null
        }.getOrElse { 0 } ?: 0
    }

    override suspend fun getHistoryList(): List<HistoryInfo> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getHistoryList()
            if (response.isSuccessful) {
                response.body()?.takeIf { it.code == "200" }?.data
            } else null
        }.getOrElse { emptyList() } ?: emptyList()
    }
}