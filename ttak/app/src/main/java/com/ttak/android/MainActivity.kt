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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ttak.android.common.monitor.ForegroundAppMonitor
import com.ttak.android.common.monitor.ForegroundMonitorService
import com.ttak.android.ui.theme.TtakTheme

/*
1. 앱 실행 시 필요한 권한들을 확인
2. 권한이 없으면 각각의 권한 요청 다이얼로그 표시
3. 모든 권한이 허용되면 모니터링 서비스 시작
4. 서비스는 백그라운드에서 2초마다 현재 실행 중인 앱을 체크하고 로그 출력
 */

class MainActivity : ComponentActivity() {
    private lateinit var foregroundAppMonitor: ForegroundAppMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foregroundAppMonitor = ForegroundAppMonitor(application)

        enableEdgeToEdge()
        setContent {
            TtakTheme {
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted && foregroundAppMonitor.hasUsageStatsPermission()) {
                        startForegroundMonitorService()
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
                            }
                        }
                    } else {
                        if (foregroundAppMonitor.hasUsageStatsPermission()) {
                            startForegroundMonitorService()
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Text(text = "앱 모니터링 실행 중")
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

    private fun startForegroundMonitorService() {
        Intent(this, ForegroundMonitorService::class.java).also { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
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