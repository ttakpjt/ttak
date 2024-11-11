package com.ttak.android

import WebSocketManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ttak.android.common.monitor.ForegroundAppMonitor
import com.ttak.android.common.monitor.ForegroundMonitorService
import com.ttak.android.common.ui.theme.TtakTheme
import com.ttak.android.common.navigation.AppNavHost
import com.ttak.android.common.navigation.NavigationManager
import com.ttak.android.common.ui.components.BottomNavigationBar
import com.ttak.android.data.worker.ApiRequestWorker
import com.ttak.android.network.socket.SocketEvent
import android.Manifest
import com.ttak.android.network.util.UserPreferences
import kotlinx.coroutines.delay

/*
1. 앱 실행 시 필요한 권한들을 확인
2. 권한이 없으면 각각의 권한 요청 다이얼로그 표시
3. 모든 권한이 허용되면 모니터링 서비스 시작
4. 서비스는 백그라운드에서 2초마다 현재 실행 중인 앱을 체크하고 로그 출력
 */

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            Toast.makeText(this, "알림 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var webSocketManager: WebSocketManager
    private lateinit var foregroundAppMonitor: ForegroundAppMonitor

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
//                getToken()
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
//            getToken()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate called")  // 추가

        // 초기화
        webSocketManager = WebSocketManager.getInstance(applicationContext)
        Log.d(TAG, "WebSocketManager initialized")  // 추가

        foregroundAppMonitor = ForegroundAppMonitor(application)

        setContent {
            TtakTheme {
                val navController = rememberNavController()

                // NavController 초기화
                LaunchedEffect(navController) {
                    NavigationManager.setNavController(navController)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // WebSocket 연결 및 이벤트 처리
                    LaunchedEffect(Unit) {
                        Log.d(TAG, "LaunchedEffect for WebSocket triggered")  // 추가
                        try {
                            Log.d(TAG, "Attempting to connect to WebSocket...")

                            // userId 로그 추가
                            val userId = UserPreferences(applicationContext).getUserId()
                            Log.d(TAG, "Current userId: $userId")  // 추가

                            webSocketManager.connect()

                            Log.d(TAG, "Starting to collect WebSocket events")  // 추가
                            webSocketManager.socketEvents.collect { event ->
                                when (event) {
                                    is SocketEvent.Connected -> {
                                        Log.d(TAG, "WebSocket connected successfully")
                                    }
                                    is SocketEvent.MessageReceived -> {
                                        Log.d(TAG, "Received WebSocket message: ${event.data}")
                                    }
                                    is SocketEvent.Disconnected -> {
                                        Log.e(TAG, "WebSocket disconnected, will attempt reconnect in 5s")  // 수정
                                        delay(5000)
                                        Log.d(TAG, "Attempting reconnection after disconnect")  // 추가
                                        webSocketManager.connect()
                                    }
                                    is SocketEvent.Error -> {
                                        Log.e(TAG, "WebSocket error occurred", event.error)
                                        Log.e(TAG, "Error details: ${event.error.message}")  // 추가
                                        delay(5000)
                                        Log.d(TAG, "Attempting reconnection after error")  // 추가
                                        webSocketManager.connect()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Critical error in WebSocket setup", e)  // 수정
                            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")  // 추가
                        }
                    }

                    Box(modifier = Modifier.padding(innerPadding)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f)) {
                                AppNavHost(navController)
                            }
                            BottomNavigationBar(navController = navController)
                        }
                    }
                }
            }
        }

        startForegroundMonitorService()
        startApiRequestWorker()
    }

    private fun checkPermissionAndStartService() {
        if (!foregroundAppMonitor.hasUsageStatsPermission()) {
            Log.d(TAG, "Usage stats permission not granted, requesting...")
            foregroundAppMonitor.requestUsageStatsPermission(this)
        } else {
            Log.d(TAG, "Usage stats permission granted, starting service...")
            startForegroundMonitorService()
            startApiRequestWorker()
        }
    }

    override fun onResume() {
        super.onResume()
        // 웹소켓은 LaunchedEffect에서 관리하므로 여기서 제거

        // 권한 체크 다시 수행
        if (foregroundAppMonitor.hasUsageStatsPermission()) {
            startForegroundMonitorService()
        }

        // 알림 권한 확인 및 요청
        askNotificationPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketManager.disconnect()
    }
//    // FCM 토큰을 수동으로 가져오는 함수
//    private fun getToken() {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val token = task.result
//                Log.d("MainActivity", "FCM 토큰: $token")
//                Toast.makeText(this, "FCM 토큰: $token", Toast.LENGTH_SHORT).show()
//
//                // FCM 토큰을 서버로 전송
//                CoroutineScope(Dispatchers.IO).launch {
//                    sendRegistrationToServer(token)
//                }
//            } else {
//                Log.w("MainActivity", "FCM 토큰 가져오기 실패", task.exception)
//            }
//        }
//    }
//
//    private suspend fun sendRegistrationToServer(token: String) {
//        Log.d(TAG, "Testing token generation: $token")
//        val json = JSONObject().apply {
//            put("token", token)
//        }
//
//        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
//        val client = OkHttpClient()
//
//        val request = Request.Builder()
//            .url("https://k11a509.p.ssafy.io/api/fcm/save")
//            .addHeader("user", "1")  // 사용자 ID를 헤더에 추가 (예시)
//            .post(requestBody)
//            .build()
//
//        try {
//            client.newCall(request).execute().use { response ->
//                if (response.isSuccessful) {
//                    Log.d(TAG, "토큰 전송 성공: ${response.body?.string()}")
//                } else {
//                    Log.e(TAG, "토큰 전송 실패: ${response.code}")
//                }
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "네트워크 요청 중 오류 발생", e)
//        }
//    }

    // 포그라운드 구동 앱 감시
    private fun startForegroundMonitorService() {
        Intent(this, ForegroundMonitorService::class.java).also { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }

    // API 요청을 위한 Worker 시작
    private fun startApiRequestWorker() {
        val apiRequestWork = OneTimeWorkRequestBuilder<ApiRequestWorker>().build()
        WorkManager.getInstance(this).enqueue(apiRequestWork)
    }
}