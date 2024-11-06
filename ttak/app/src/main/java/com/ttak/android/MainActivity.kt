package com.ttak.android

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
import com.ttak.android.network.socket.WebSocketManager
import android.Manifest
import com.google.firebase.messaging.FirebaseMessaging

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

    private val webSocketManager = WebSocketManager.getInstance()
    private lateinit var foregroundAppMonitor: ForegroundAppMonitor

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                getToken()
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            getToken()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ForegroundAppMonitor 초기화
        foregroundAppMonitor = ForegroundAppMonitor(application)

        setContent {
            TtakTheme {
                val navController = rememberNavController()

                // 권한 체크 및 서비스 시작
                LaunchedEffect(Unit) {
                    checkPermissionAndStartService()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // NavigationManager 초기화를 여기로 이동
                    LaunchedEffect(navController) {
                        Log.d(TAG, "Setting NavController in MainActivity")
                        NavigationManager.setNavController(navController)
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

                // 웹소켓 이벤트 수신 처리
                LaunchedEffect(Unit) {
                    webSocketManager.socketEvents.collect { event ->
                        when (event) {
                            is SocketEvent.Connected -> {
                                Log.d(TAG, "WebSocket connected")
                            }
                            is SocketEvent.MessageReceived -> {
                                Log.d(TAG, "WebSocket message: ${event.data}")  // data로 수정
                            }
                            is SocketEvent.Disconnected -> {
                                Log.d(TAG, "WebSocket disconnected")
                            }
                            is SocketEvent.Error -> {
                                Log.e(TAG, "WebSocket error", event.error)  // error로 수정
                            }
                        }
                    }
                }

                startForegroundMonitorService()
                startApiRequestWorker()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Activity가 완전히 종료될 때도 연결 해제
        webSocketManager.disconnect()
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
        webSocketManager.connect()

        // 권한 체크 다시 수행
        if (foregroundAppMonitor.hasUsageStatsPermission()) {
            startForegroundMonitorService()
        }

        // 알림 권환 확인 및 요청
        askNotificationPermission()
    }

    // FCM 토큰을 수동으로 가져오는 함수
    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("MainActivity", "FCM 토큰: $token")
                Toast.makeText(this, "FCM 토큰: $token", Toast.LENGTH_SHORT).show()
            } else {
                Log.w("MainActivity", "FCM 토큰 가져오기 실패", task.exception)
            }
        }
    }
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