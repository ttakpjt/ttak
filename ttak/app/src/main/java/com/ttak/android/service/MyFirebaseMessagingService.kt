//package com.ttak.android.service
//
//import android.Manifest
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.provider.Settings
//import android.util.Log
//import androidx.core.app.ActivityCompat
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import com.google.firebase.messaging.remoteMessage
//import okhttp3.Call
//import okhttp3.Callback
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import okhttp3.Response
//import org.json.JSONObject
//import java.io.IOException
////import com.ttak.android.UnityPlayerActivity
//
//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    /**
//     * Called if the FCM registration token is updated. This may occur if the security of
//     * the previous token had been compromised. Note that this is called when the
//     * FCM registration token is initially generated so this is where you would retrieve the token.
//     */
//    override fun onNewToken(token: String) {
//        Log.d("MyFirebaseMessagingService", "Refreshed token: $token")
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // FCM registration token to your app server.
//        sendRegistrationToServer(token)
//    }
//
//    private fun sendRegistrationToServer(token: String) {
//        // 서버 전송 없이 토큰을 로그로 출력하여 테스트
//        Log.d("MyFirebaseMessagingService", "Testing token generation: $token")
//        val deviceSerialNumber = getDeviceSerialNumber()
//        val json = JSONObject().apply {
//            put("deviceSerialNumber", deviceSerialNumber)
//            put("token", token)
//        }
//
//
//        // JSON 데이터를 문자열로 변환 후, 실제 JSON을 포함한 requestBody 생성
//        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
//        Log.d("MyFirebaseMessagingService", "Request body: $requestBody")
//
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("https://k11a509.p.ssafy.io/api/fcm/save")
////            .addHeader("user", "1")
//            .post(requestBody)
//            .build()
//
//        Log.d("MyFirebaseMessagingService", "Request: $request")
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                Log.e("MyFirebaseMessagingService", "토큰 전송 실패: ${e.message}")
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                if (response.isSuccessful) {
//                    Log.d("MyFirebaseMessagingService", "토큰 전송 성공: ${response.body?.string()}")
//                } else {
//                    Log.e("MyFirebaseMessagingService", "토큰 전송 실패: ${response.code}")
//                }
//            }
//        })
//    }
//
//    // 기기 고유 ID를 가져오는 메서드
//    private fun getDeviceSerialNumber(): String {
//        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
//    }
//
//    // 메시지 수신 시 호출되는 메서드 추가
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        Log.d("MyFirebaseMessagingService", "FCM 메시지 수신")
//
//        // 1. 데이터 메시지인지 확인
//        if (remoteMessage.data.isNotEmpty()) {
//            Log.d("MyFirebaseMessagingService", "데이터 메시지: ${remoteMessage.data}")
//            handleDataMessage(remoteMessage.data)
//        } else {
//            Log.d("MyFirebaseMessagingService", "데이터는 아닙니다")
//        }
//
//        // 2. 알림 메시지인지 확인
//        remoteMessage.notification?.let {
//            Log.d("MyFirebaseMessagingService", "알림 메시지: ${it.title}, ${it.body}")
//            handleNotificationMessage(it)
//        }
//    }
//
//    // 데이터 메시지를 처리하는 함수
//    private fun handleDataMessage(data: Map<String, String>) {
//        val customData = data["animation"] ?: "waterBalloon" // 예: 데이터 메시지의 특정 키 값 가져오기
//        Log.d("MyFirebaseMessagingService", "데이터 메시지 - customKey: $customData")
//        // 필요한 데이터 처리 로직 추가
////
////        val animationType = data["animation"] ?: "waterBomb"
////
////        if (animationType == "waterBomb") {
////            // UnityPlayerActivity를 실행하여 Unity 애니메이션을 실행
//////            val intent = Intent(this, UnityPlayerActivity::class.java).apply {
////                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 백그라운드에서 Activity 실행
////            }
////            startActivity(intent)
////        }
//    }
//
//    // 알림 메시지를 처리하는 함수
//    private fun handleNotificationMessage(notification: RemoteMessage.Notification) {
//        val title = notification.title ?: "알림 제목 없음"
//        val body = notification.body ?: "알림 내용 없음"
//        Log.d("MyFirebaseMessagingService", "알림 제목: $title, 내용: $body")
//
//        // Android 13 이상에서 POST_NOTIFICATIONS 권한 체크
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                Log.w("MyFirebaseMessagingService", "알림 권한이 없어 알림을 표시할 수 없습니다.")
//                // 권한이 없는 경우는 알림을 표시하지 않고 종료
//                return
//            }
//        }
//
//        // 알림 채널 ID와 이름을 설정
//        val channelId = "fcm_default_channel"
//        val channelName = "Default Channel"
//
//        // 알림 채널 생성
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                channelName,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        // 알림을 생성하여 표시
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setContentTitle(title)
//            .setContentText(body)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setAutoCancel(true)
//
//        with(NotificationManagerCompat.from(this)) {
//            notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
//        }
//    }
//}

package com.ttak.android.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
        val deviceSerialNumber = getDeviceSerialNumber()
        val json = JSONObject().apply {
            put("deviceSerialNumber", deviceSerialNumber)
            put("token", token)
        }

        // JSON 데이터를 문자열로 변환 후, 실제 JSON을 포함한 requestBody 생성
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        Log.d("MyFirebaseMessagingService", "Request body: $requestBody")

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://k11a509.p.ssafy.io/api/fcm/save")
//            .addHeader("user", "1")
            .post(requestBody)
            .build()

        Log.d("MyFirebaseMessagingService", "Request: $request")
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

    // FCM 메시지 수신 처리
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Wake Lock 획득으로 확실한 처리 보장
        val wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::FCMWakeLock").apply {
                acquire(10*1000L) // 10초 동안 WakeLock 유지
            }
        }

        try {
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

            // 1. 데이터 메시지 처리
            if (remoteMessage.data.isNotEmpty()) {
                handleDataMessage(remoteMessage.data)
            }

            // 2. 알림 메시지 처리
            remoteMessage.notification?.let {
                handleNotificationMessage(it)
            }
        } finally {
            wakeLock.release()
        }
    }


    // 데이터 메시지 처리 (기존 코드 유지)
    private fun handleDataMessage(data: Map<String, String>) {
        val customData = data["animation"] ?: "waterBalloon"
        Log.d("MyFirebaseMessagingService", "데이터 메시지 - customKey: $customData")
    }

    private fun handleNotificationMessage(notification: RemoteMessage.Notification) {
        val title = notification.title ?: "TTAK"
        val body = notification.body ?: "알림 내용 없음"

        val channelId = "default_channel"

        // 알림 채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "긴급 알림",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "중요한 알림을 표시하는 채널입니다"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)  // 방해 금지 모드 무시
                importance = NotificationManager.IMPORTANCE_HIGH

                // 소리 설정
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // 메인 인텐트
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("from_notification", true)  // 알림을 통한 실행 표시
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 전체화면 인텐트
        val fullScreenIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("from_notification", true)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            1,
            fullScreenIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(pendingIntent)
            // 추가 설정
            .setOngoing(true)  // 사용자가 스와이프로 제거할 수 없게 설정
            .setTimeoutAfter(10000)  // 10초 후 자동으로 제거

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 기존 알림 모두 제거 후 새 알림 표시
            NotificationManagerCompat.from(this).apply {
                cancelAll()  // 기존 알림 모두 제거
                notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
            }
        }
    }
}