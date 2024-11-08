package com.ttak.android.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.remoteMessage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d("MyFirebaseMessagingService", "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // 서버 전송 없이 토큰을 로그로 출력하여 테스트
        Log.d("MyFirebaseMessagingService", "Testing token generation: $token")
        val json = JSONObject()
        json.put("token", token)

        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://k11a509.p.ssafy.io/api/fcm/save")
            .addHeader("user", "1")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                Log.d("MyFirebaseMessagingService", "토큰 전송 성공: ${response.body?.toString()}")
            } else {
                Log.e("MyFirebaseMessagingService", "토큰 전송 실패: ${response.code}")
            }
        }
    }

    // 메시지 수신 시 호출되는 메서드 추가
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("MyFirebaseMessagingService", "FCM 메시지 수신")

        // 1. 데이터 메시지인지 확인
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("MyFirebaseMessagingService", "데이터 메시지: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        } else {
            Log.d("MyFirebaseMessagingService", "데이터는 아닙니다")
        }

        // 2. 알림 메시지인지 확인
        remoteMessage.notification?.let {
            Log.d("MyFirebaseMessagingService", "알림 메시지: ${it.title}, ${it.body}")
            handleNotificationMessage(it)
        }
    }

    // 데이터 메시지를 처리하는 함수
    private fun handleDataMessage(data: Map<String, String>) {
        val customData = data["animation"] ?: "waterBalloon" // 예: 데이터 메시지의 특정 키 값 가져오기
        Log.d("MyFirebaseMessagingService", "데이터 메시지 - customKey: $customData")
        // 필요한 데이터 처리 로직 추가
    }

    // 알림 메시지를 처리하는 함수
    private fun handleNotificationMessage(notification: RemoteMessage.Notification) {
        val title = notification.title ?: "알림 제목 없음"
        val body = notification.body ?: "알림 내용 없음"
        Log.d("MyFirebaseMessagingService", "알림 제목: $title, 내용: $body")
        // 알림을 UI에 표시하거나 추가 처리가 필요할 경우 로직 추가
    }
}