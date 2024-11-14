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
import android.app.KeyguardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ttak.android.network.implementation.FriendApiImpl
import com.ttak.android.features.observer.viewmodel.FriendStoryViewModel
import com.ttak.android.network.util.ApiConfig
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private var doubleBackToExitPressedOnce = false
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var foregroundAppMonitor: ForegroundAppMonitor
    private var isServiceRunning = false
    private lateinit var friendStoryViewModel: FriendStoryViewModel

    // 알림 권한 요청을 위한 launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
        } else {
            Log.w(TAG, "Notification permission denied")
            Toast.makeText(this, "알림 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 알림 권한 확인 및 요청 함수
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "Notification permission already granted")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Log.d(TAG, "Should show notification permission rationale")
                    Toast.makeText(
                        this,
                        "알림을 받으시려면 알림 권한이 필요합니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    Log.d(TAG, "Requesting notification permission")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        // 뒤로 가기 버튼 동작 설정
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finish()
                } else {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(
                        this@MainActivity,
                        "한 번 더 누르면 앱을 종료합니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000)
                }
            }
        })

        // WebSocket 및 ForegroundAppMonitor 초기화
        webSocketManager = WebSocketManager.getInstance(applicationContext)
        foregroundAppMonitor = ForegroundAppMonitor(application)

        // ViewModel 초기화
        initializeViewModel()

        // 권한 확인 및 서비스 시작
        checkPermissionAndStartService()

        // 알림 권한 요청
        askNotificationPermission()

        // UI 설정
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
                        handleWebSocketConnection()
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
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service", e)
            isServiceRunning = false
        }
    }

    private fun initializeViewModel() {
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
    }

    private suspend fun handleWebSocketConnection() {
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

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")

        // 권한이 있고 서비스가 실행중이 아닌 경우에만 시작
        if (foregroundAppMonitor.hasUsageStatsPermission() && !isServiceRunning) {
            safeStartService()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
        webSocketManager.disconnect()
        isServiceRunning = false
    }
}