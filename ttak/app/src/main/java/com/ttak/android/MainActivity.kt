package com.ttak.android

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ttak.android.common.monitor.ForegroundMonitorService
import com.ttak.android.common.ui.theme.TtakTheme
import com.ttak.android.common.navigation.AppNavHost
import com.ttak.android.common.navigation.NavigationManager
import com.ttak.android.common.ui.components.BottomNavigationBar
import com.ttak.android.data.worker.ApiRequestWorker

/*
1. 앱 실행 시 필요한 권한들을 확인
2. 권한이 없으면 각각의 권한 요청 다이얼로그 표시
3. 모든 권한이 허용되면 모니터링 서비스 시작
4. 서비스는 백그라운드에서 2초마다 현재 실행 중인 앱을 체크하고 로그 출력
 */

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TtakTheme {
                val navController = rememberNavController()

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
                startForegroundMonitorService()
                startApiRequestWorker()
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