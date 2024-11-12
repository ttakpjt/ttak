package com.ttak.android.data.repository.history

import com.ttak.android.domain.model.history.HistoryInfo

interface HistoryRepository {

    suspend fun getWeeklyPickCount(): Int
    suspend fun getWeeklyWatchingCount(): Int
    suspend fun getMessages(): List<HistoryInfo>
    suspend fun sendMessage(message: String)
}