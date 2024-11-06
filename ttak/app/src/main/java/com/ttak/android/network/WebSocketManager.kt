package com.ttak.android.network

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.*
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class WebSocketManager private constructor() {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val _socketEvents = MutableSharedFlow<SocketEvent>()
    val socketEvents = _socketEvents.asSharedFlow()

    companion object {
        private const val TAG = "WebSocketManager"
        // 실제 서버 URL로 변경 필요
        private const val SOCKET_URL = "ws://your-server-url/ws"
        const val FRIEND_STORY_TOPIC = "/topic/friend-story"  // 토픽 이름 변경
        const val STATUS_FALSE = 0    // hasNewStory = false
        const val STATUS_TRUE = 1     // hasNewStory = true


        @Volatile
        private var instance: WebSocketManager? = null

        fun getInstance(): WebSocketManager {
            return instance ?: synchronized(this) {
                instance ?: WebSocketManager().also { instance = it }
            }
        }
    }

    fun connect() {
        val request = Request.Builder()
            .url(SOCKET_URL)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket Connected")
                CoroutineScope(Dispatchers.IO).launch {
                    _socketEvents.emit(SocketEvent.Connected)
                    subscribeFriendStoryStatus()  // 메서드 이름 변경
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Message received: $text")
                CoroutineScope(Dispatchers.IO).launch {
                    _socketEvents.emit(SocketEvent.MessageReceived(text))
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket Closing: $reason")
                CoroutineScope(Dispatchers.IO).launch {
                    _socketEvents.emit(SocketEvent.Disconnected)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket Error", t)
                CoroutineScope(Dispatchers.IO).launch {
                    _socketEvents.emit(SocketEvent.Error(t))
                }
            }
        })
    }

    private suspend fun subscribeFriendStoryStatus() {
        val subscribeMessage = """
            {
                "destination": "$FRIEND_STORY_TOPIC",
                "id": "sub-detection",
                "type": "SUBSCRIBE"
            }
        """.trimIndent()
        Log.d(TAG, "Subscribing to friend story status: $subscribeMessage")
        webSocket?.send(subscribeMessage)
    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting WebSocket")
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }

    // 상태 변경을 서버에 전송하는 함수
    fun sendStatusUpdate(status: Int) {
        val statusMessage = """
            {
                "destination": "/friends/status",
                "body": {
                    "status": $status
                }
            }
        """.trimIndent()
        Log.d(TAG, "Sending status update message: $statusMessage")
        webSocket?.send(statusMessage)
    }
}
