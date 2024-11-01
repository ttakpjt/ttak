package com.ttak.android

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ttak.android.common.monitor.ForegroundAppMonitor
import com.ttak.android.common.monitor.ForegroundMonitorService
import com.ttak.android.common.ui.theme.TtakTheme
import com.ttak.android.common.navigation.AppNavHost
import com.ttak.android.common.ui.components.BottomNavigationBar
import com.ttak.android.common.ui.theme.Black
import com.ttak.android.data.worker.ApiRequestWorker // ApiRequestWorker 임포트

class MainActivity : ComponentActivity() {
    private lateinit var foregroundAppMonitor: ForegroundAppMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foregroundAppMonitor = ForegroundAppMonitor(application)

        enableEdgeToEdge()
        setContent {
            TtakTheme {
                val navController = rememberNavController()
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted && foregroundAppMonitor.hasUsageStatsPermission()) {
                        startForegroundMonitorService()
                        startApiRequestWorker() // ApiRequestWorker 호출
                    }
                }
                val showPermissionDialog = remember { mutableStateOf(!foregroundAppMonitor.hasUsageStatsPermission()) }
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                            PackageManager.PERMISSION_GRANTED) {
                            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            if (foregroundAppMonitor.hasUsageStatsPermission()) {
                                startForegroundMonitorService()
                                startApiRequestWorker() // ApiRequestWorker 호출
                            }
                        }
                    } else {
                        if (foregroundAppMonitor.hasUsageStatsPermission()) {
                            startForegroundMonitorService()
                            startApiRequestWorker() // ApiRequestWorker 호출
                        }
                    }
                }
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Black),
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f)) {
                                AppNavHost(navController)
                            }
                            BottomNavigationBar(navController = navController)
                        }
                    }
                    if (showPermissionDialog.value) {
                        PermissionDialog(
                            onConfirm = {
                                foregroundAppMonitor.requestUsageStatsPermission(this@MainActivity)
                                showPermissionDialog.value = false
                            },
                            onDismiss = {
                                showPermissionDialog.value = false
                            }
                        )
                    }
                }
            }
        }
    }

    // Foreground 모니터링 서비스 시작 함수
    private fun startForegroundMonitorService() {
        Intent(this, ForegroundMonitorService::class.java).also { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }

    // ApiRequestWorker 실행 함수
    private fun startApiRequestWorker() {
        val apiRequestWork = OneTimeWorkRequestBuilder<ApiRequestWorker>().build()
        WorkManager.getInstance(this).enqueue(apiRequestWork)
    }
}

@Composable
fun PermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("권한 필요") },
        text = { Text("앱 사용 현황 접근 권한이 필요합니다. 설정에서 권한을 허용해주세요.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("설정으로 이동")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
