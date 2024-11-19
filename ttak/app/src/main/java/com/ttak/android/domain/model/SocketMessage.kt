package com.ttak.android.domain.model

data class SocketMessage(
    val type: String,
    val destination: String,
    val payload: String
)