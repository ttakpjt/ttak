package com.ttak.android.features.screentime.ui.screens

import SmallGoalCard
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ttak.android.features.screentime.ui.components.*
import com.ttak.android.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttak.android.features.screentime.viewmodel.ScreenTimeViewModel
import com.ttak.android.features.screentime.viewmodel.ScreenTimeViewModelFactory

@Composable
fun ScreenTimeScreen() {
    val context = LocalContext.current
    val viewModel: ScreenTimeViewModel = viewModel(
        factory = ScreenTimeViewModelFactory(context)
    )
    val screenTimeData = viewModel.screenTimeData.collectAsState().value

    screenTimeData?.let { data ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 1. 앱 사용 약속 시간 표시
            SmallGoalCard(
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 사용 시간 변화 표시
            ScreenTimeChangeComponent(username = "remember the name", hoursDifference = data.todayUsageDifference)

            Spacer(modifier = Modifier.height(16.dp))

            // 3. 오늘 사용 시간 도넛 차트
            TodayAppUsageChartComponent(topSixAppsUsage = data.topSixAppsUsage)

            Spacer(modifier = Modifier.height(16.dp))

            // 4. 기간별 사용 시간 요약
            TimeUsageSummaryComponent(monthUsage = data.monthUsage, weekUsage = data.weekUsage, todayUsage = data.todayUsage)

            Spacer(modifier = Modifier.height(16.dp))

            // 5. 요일별 스마트폰 사용량 막대 차트
            WeeklyUsageBarChartComponent(data.dailyUsageList)
        }
    } ?: Text("Loading...") // 데이터가 로딩 중일 때 표시
}
