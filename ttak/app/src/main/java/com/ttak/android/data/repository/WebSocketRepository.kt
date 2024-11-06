package com.ttak.android.data.repository

import android.util.Log
import com.google.gson.Gson
import com.ttak.android.domain.model.FriendStatus
import com.ttak.android.domain.model.SocketMessage
import com.ttak.android.network.socket.WebSocketManager

class WebSocketRepository private constructor() {
    private val TAG = "WebSocketRepository"
    private val webSocketManager = WebSocketManager.getInstance()
    val socketEvents = webSocketManager.socketEvents

    companion object {
        @Volatile
        private var instance: WebSocketRepository? = null

        fun getInstance(): WebSocketRepository {
            return instance ?: synchronized(this) {
                instance ?: WebSocketRepository().also { instance = it }
            }
        }
    }

    suspend fun handleMessage(text: String) {
        try {
            val message = Gson().fromJson(text, SocketMessage::class.java)
            Log.d(TAG, "Handling message: $message")
            if (message.type == "MESSAGE" &&
                message.destination == WebSocketManager.FRIEND_STORY_TOPIC) {
                val status = Gson().fromJson(message.payload, FriendStatus::class.java)
                when (status.status) {
                    WebSocketManager.STATUS_TRUE -> {
                        Log.d(TAG, "User ${status.id} is using restricted app")
                    }
                    WebSocketManager.STATUS_FALSE -> {
                        Log.d(TAG, "User ${status.id} is not using restricted app")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling message", e)
        }
    }

    fun connect() = webSocketManager.connect()
    fun disconnect() = webSocketManager.disconnect()
}