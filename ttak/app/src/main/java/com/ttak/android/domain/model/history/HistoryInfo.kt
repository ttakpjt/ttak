package com.ttak.android.domain.model.history

data class HistoryInfo(
    val id: String,
    val content: String,
    val sender: String,
    val timestamp: String,
    val type: HistoryType
)
