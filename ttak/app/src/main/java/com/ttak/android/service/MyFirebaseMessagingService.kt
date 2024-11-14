package com.ttak.android.service

import android.Manifest
import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ttak.android.MainActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import com.ttak.android.R

class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val CHANNEL_ID = "incoming_call_channel"
    }

    override fun onCreate() {
        super.onCreate()
        deleteAndCreateChannel()
    }

    private fun deleteAndCreateChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 기존 채널 삭제
            notificationManager.deleteNotificationChannel(CHANNEL_ID)

            // 새 채널 생성
            val channel = NotificationChannel(
                CHANNEL_ID,
                "수신 알림",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(true)
                enableLights(true)
                enableVibration(true)
                description = "긴급한 수신 알림"
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                importance = NotificationManager.IMPORTANCE_HIGH
                setBypassDnd(true)

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("MyFirebaseMessagingService", "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        Log.d("MyFirebaseMessagingService", "Testing token generation: $token")
        val deviceSerialNumber = getDeviceSerialNumber()
        val json = JSONObject().apply {
            put("deviceSerialNumber", deviceSerialNumber)
            put("token", token)
        }

        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        Log.d("MyFirebaseMessagingService", "Request body: $requestBody")

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://k11a509.p.ssafy.io/api/fcm/save")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MyFirebaseMessagingService", "토큰 전송 실패: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("MyFirebaseMessagingService", "토큰 전송 성공: ${response.body?.string()}")
                } else {
                    Log.e("MyFirebaseMessagingService", "토큰 전송 실패: ${response.code}")
                }
            }
        })
    }

    private fun getDeviceSerialNumber(): String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 채널 재생성
        deleteAndCreateChannel()

        Log.d("MyFirebaseMessagingService", """
            |----------------------------------------
            |           FCM Message Debug
            |----------------------------------------
            |[Notification]
            |   Title: ${remoteMessage.notification?.title}
            |   Body: ${remoteMessage.notification?.body}
            |   Channel: ${remoteMessage.notification?.channelId}
            |[Priority] ${remoteMessage.priority}
            |[Data] ${remoteMessage.data}
            |----------------------------------------
        """.trimMargin())

        val notification = remoteMessage.notification
        if (notification != null) {
            try {
                // 데이터 메시지 처리
                if (remoteMessage.data.isNotEmpty()) {
                    handleDataMessage(remoteMessage.data)
                }

                // 1. 인텐트 설정
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }

                val pendingIntent = PendingIntent.getActivity(
                    this,
                    System.currentTimeMillis().toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // 2. 이미지 설정
                val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ttak_black_text)

                // 3. 알림 스타일 설정
                val style = NotificationCompat.BigTextStyle()
                    .bigText(notification.body)
                    .setBigContentTitle(notification.title)
                    .setSummaryText("새로운 메시지")

                // 4. 알림 빌더 설정
                val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ttak_black_text)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(notification.title)
                    .setContentText(notification.body)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent)
                    .setFullScreenIntent(pendingIntent, true)
                    .setWhen(System.currentTimeMillis())
                    .setShowWhen(true)
                    .setStyle(style)
                    .setColor(ContextCompat.getColor(this, R.color.red))
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .addAction(
                        R.drawable.ttak_black_text,
                        "바로 확인",
                        pendingIntent
                    )
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(longArrayOf(0, 500, 200, 500))

                // 5. 알림 표시
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // 동일한 채널의 이전 알림 모두 제거
                    val notificationManager = NotificationManagerCompat.from(this)
                    notificationManager.cancelAll()

                    // 새 알림 생성 - 고유한 ID 사용
                    val notificationId = System.currentTimeMillis().toInt()

                    // 메인 스레드에서 알림 표시
                    Handler(Looper.getMainLooper()).post {
                        notificationManager.notify(notificationId, notificationBuilder.build())
                    }
                }

            } catch (e: Exception) {
                Log.e("FCM", "Error showing notification", e)
            }
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val customData = data["animation"] ?: "waterBalloon"
        Log.d("MyFirebaseMessagingService", "데이터 메시지 - customKey: $customData")
    }
}