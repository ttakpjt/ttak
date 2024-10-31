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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.ttak.android.common.monitor.ForegroundAppMonitor
import com.ttak.android.common.monitor.ForegroundMonitorService
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ttak.android.ui.theme.TtakTheme

/*
1. 앱 실행 시 필요한 권한들을 확인
2. 권한이 없으면 각각의 권한 요청 다이얼로그 표시
3. 모든 권한이 허용되면 모니터링 서비스 시작
4. 서비스는 백그라운드에서 2초마다 현재 실행 중인 앱을 체크하고 로그 출력
 */
import com.ttak.android.common.ui.theme.TtakTheme
import androidx.compose.ui.graphics.Color
import com.ttak.android.common.ui.components.AppButton
import com.ttak.android.common.ui.components.AppSearchBar
import com.ttak.android.common.ui.theme.TtakTheme
import com.ttak.android.common.navigation.AppNavHost
import com.ttak.android.common.ui.components.BottomNavigationBar

class MainActivity : ComponentActivity() {
    private lateinit var foregroundAppMonitor: ForegroundAppMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foregroundAppMonitor = ForegroundAppMonitor(application)

        enableEdgeToEdge()
        setContent {
            // TtakTheme으로 앱 전체 UI를 감쌈
            TtakTheme {
                val navController = rememberNavController()
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Text(text = "앱 모니터링 실행 중")
                    }

                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f)) {
                                AppNavHost(navController)
                            }
                            BottomNavigationBar(navController = navController)  // 네비게이션 바 직접 위치
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

    private fun startForegroundMonitorService() {
        Intent(this, ForegroundMonitorService::class.java).also { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
                AppButton(
                    text = "보내기",
                    onClick = { /* 버튼 클릭 시 동작 */ },
                    backgroundColor = Color.Green,
                    contentColor = Color.Black
                )
            }
        }
    )
}

@Composable
fun TestProfileItem() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "사용자 이름", style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
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