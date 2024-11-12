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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ttak.android.network.implementation.FriendApiImpl
import com.ttak.android.features.observer.viewmodel.FriendStoryViewModel
import com.ttak.android.network.util.ApiConfig
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private var doubleBackToExitPressedOnce = false // 두 번 눌렀는지 체크하는 변수
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var foregroundAppMonitor: ForegroundAppMonitor
    private var isServiceRunning = false
    private lateinit var friendStoryViewModel: FriendStoryViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            Toast.makeText(this, "알림 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is already granted
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // TODO: display an educational UI
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        // 초기화
        webSocketManager = WebSocketManager.getInstance(applicationContext)
        foregroundAppMonitor = ForegroundAppMonitor(application)

        // ViewModel 초기화
        val friendApi = ApiConfig.createFriendApi(applicationContext)
        val repository = FriendApiImpl(friendApi)
        friendStoryViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FriendStoryViewModel(repository) as T
                }
            }
        )[FriendStoryViewModel::class.java]

        // 권한 확인 후 서비스 시작
        checkPermissionAndStartService()

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
                    LaunchedEffect(Unit) {
                        try {
                            webSocketManager.connect()
                            webSocketManager.socketEvents.collect { event ->
                                when (event) {
                                    is SocketEvent.Connected ->
                                        Log.d(TAG, "WebSocket connected successfully")
                                    is SocketEvent.MessageReceived -> {
                                        Log.d(TAG, "Received WebSocket message: ${event.data}")
                                        friendStoryViewModel.handleWebSocketMessage(event)
                                    }
                                    is SocketEvent.Disconnected -> {
                                        Log.e(TAG, "WebSocket disconnected, will attempt reconnect in 5s")
                                        delay(5000)
                                        webSocketManager.connect()
                                    }
                                    is SocketEvent.Error -> {
                                        Log.e(TAG, "WebSocket error occurred", event.error)
                                        delay(5000)
                                        webSocketManager.connect()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Critical error in WebSocket setup", e)
                        }
                    }

                    Box(modifier = Modifier.padding(innerPadding)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f)) {
                                AppNavHost(
                                    navController = navController,
                                    friendStoryViewModel = friendStoryViewModel
                                )
                            }
                            BottomNavigationBar(navController = navController)
                        }
                    }
                }
            }
        }
    }

    private fun checkPermissionAndStartService() {
        if (!foregroundAppMonitor.hasUsageStatsPermission()) {
            Log.d(TAG, "Usage stats permission not granted, requesting...")
            foregroundAppMonitor.requestUsageStatsPermission(this)
        } else {
            Log.d(TAG, "Usage stats permission granted")
            safeStartService()
        }
    }

    private fun safeStartService() {
        if (isServiceRunning) {
            Log.d(TAG, "Service is already running")
            return
        }

        try {
            Intent(this, ForegroundMonitorService::class.java).also { intent ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
            }
            isServiceRunning = true
            Log.d(TAG, "Service started successfully")

            // API 워커는 서비스 시작과 함께 한 번만 실행
            startApiRequestWorker()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service", e)
            isServiceRunning = false
        }
    }

    override fun onResume() {
        super.onResume()

        // 권한이 있고 서비스가 실행중이 아닌 경우에만 시작
        if (foregroundAppMonitor.hasUsageStatsPermission() && !isServiceRunning) {
            safeStartService()
        }

        askNotificationPermission()
    }

    // 뒤로 가기 버튼을 처리하는 메서드
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed() // 두 번째로 뒤로 가기 버튼을 눌렀을 때 앱 종료
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "한 번 더 누르면 앱을 종료합니다.", Toast.LENGTH_SHORT).show()

        // 2초 이내에 다시 뒤로 가기 버튼을 누르면 앱을 종료
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false // 2초가 지나면 다시 초기화
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
        webSocketManager.disconnect()
        // 서비스는 별도로 종료하지 않고 계속 실행
        isServiceRunning = false
    }

    private fun startApiRequestWorker() {
        val apiRequestWork = OneTimeWorkRequestBuilder<ApiRequestWorker>().build()
        WorkManager.getInstance(this).enqueue(apiRequestWork)
        Log.d(TAG, "API request worker started")
    }
}