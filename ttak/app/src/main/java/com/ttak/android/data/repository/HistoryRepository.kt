package com.ttak.android.data.repository

import com.ttak.android.domain.model.HistoryInfo

interface HistoryRepository {

    suspend fun getWeeklyPickCount(): Int
    suspend fun getWeeklyWatchingCount(): Int
    suspend fun getMessages(): List<HistoryInfo>
    suspend fun sendMessage(message: String)
}