package com.ttak.android.domain.model.history

data class HistoryListResponse(
    val code: String,
    val message: String,
    val data: List<HistoryInfo>
)
