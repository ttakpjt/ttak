package com.ttak.android.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.ttak.android.domain.model.FriendStatus
import com.ttak.android.domain.model.SocketMessage

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
                message.destination == WebSocketManager.FRIEND_STATUS_TOPIC) {
                val status = Gson().fromJson(message.payload, FriendStatus::class.java)
                when (status.status) {
                    WebSocketManager.STATUS_TRUE -> {
                        Log.d(TAG, "User ${status.id} is online")
                    }
                    WebSocketManager.STATUS_FALSE -> {
                        Log.d(TAG, "User ${status.id} is offline")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling message", e)
        }
    }

    // context 파라미터 추가
    fun connect(context: Context) = webSocketManager.connect(context)

    fun disconnect() = webSocketManager.disconnect()
}