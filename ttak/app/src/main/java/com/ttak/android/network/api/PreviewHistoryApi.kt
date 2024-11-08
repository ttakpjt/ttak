package com.ttak.android.network.api

import com.ttak.android.data.repository.HistoryRepository
import com.ttak.android.domain.model.HistoryInfo
import com.ttak.android.domain.model.HistoryType

class PreviewHistoryApi : HistoryRepository {

    override suspend fun getMessages(): List<HistoryInfo> {
        return listOf(
            HistoryInfo(
                id = "1",
                sender = "탁싸피",
                content = "안녕하세요!",
                timestamp = (System.currentTimeMillis() - 3600000).toString(),
                type = HistoryType.USER_MESSAGE
            ),
            HistoryInfo(
                id = "2",
                sender = "황싸피",
                content = "열심히 하고 있나요?",
                timestamp = (System.currentTimeMillis() - 7200000).toString(),
                type = HistoryType.USER_MESSAGE
            ),
            HistoryInfo(
                id = "3",
                sender = "김싸피",
                content = "화이팅!",
                timestamp = System.currentTimeMillis().toString(),
                type = HistoryType.SYSTEM_NOTIFICATION
            )
        )
    }

    override suspend fun sendMessage(message: String) {
    }
}