package com.ttak.android.network

sealed class SocketEvent {
    object Connected : SocketEvent()
    object Disconnected : SocketEvent()
    data class MessageReceived(val data: String) : SocketEvent()
    data class Error(val error: Throwable) : SocketEvent()
}