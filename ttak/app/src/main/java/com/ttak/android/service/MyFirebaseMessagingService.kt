package com.ttak.android.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ttak.android.MainActivity
import com.ttak.android.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

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
        return android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ANDROID_ID)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data

        if (data.containsKey("animation")) {
            Log.d("MyFirebaseMessagingService", "Playing embedded video animation")

            Intent(this, AnimationOverlayService::class.java).also { intent ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
            }
        } else {
            Log.d("MyFirebaseMessagingService", "No animation key found in data message.")
        }
    }

//        when {
//            // animation 키가 있으면 이펙트 실행
//            data.containsKey("animation") -> {
//                Log.d("MyFirebaseMessagingService", "Processing effect: ${data["animation"]}")
//                when (data["animation"]?.uppercase()) {
//                    "WATER_BOOM" -> {
//                        Intent(this, AnimationOverlayService::class.java).apply {
//                            putExtra("animation", data["animation"])
//                        }.also { intent ->
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                startForegroundService(intent)
//                            } else {
//                                startService(intent)
//                            }
//                        }
//                    }
//                    else -> {
//                        Log.d("MyFirebaseMessagingService", "No valid animation key in FCM data message.")
//                    }
//                }
//            }
//            // 일반 메시지 처리
//            else -> {
//                val title = data["title"] ?: "새로운 알림"
//                val body = data["body"] ?: ""
//                Log.d("MyFirebaseMessagingService", "Messsage: ${body}")
//
//                val intent = Intent(this, MainActivity::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                }
//
//                val pendingIntent = PendingIntent.getActivity(
//                    this,
//                    System.currentTimeMillis().toInt(),
//                    intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                )
//
//                val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setSmallIcon(R.drawable.ttak_black_text)
//                    .setContentTitle(title)
//                    .setContentText(body)
//                    .setAutoCancel(true)
//                    .setDefaults(NotificationCompat.DEFAULT_ALL)
//                    .setPriority(NotificationCompat.PRIORITY_MAX)
//                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                    .setContentIntent(pendingIntent)
//                    .setFullScreenIntent(pendingIntent, true)
//                    .setWhen(System.currentTimeMillis())
//                    .setShowWhen(true)
//
//                if (ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.POST_NOTIFICATIONS
//                    ) == PackageManager.PERMISSION_GRANTED
//                ) {
//                    val notificationManager = NotificationManagerCompat.from(this)
//                    val notificationId = System.currentTimeMillis().toInt()
//                    notificationManager.notify(notificationId, notificationBuilder.build())
//                }
//            }
//        }
//    }
}