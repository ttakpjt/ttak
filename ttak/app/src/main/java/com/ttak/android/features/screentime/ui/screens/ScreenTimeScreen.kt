package com.ttak.android.features.screentime.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ttak.android.features.screentime.ui.components.*
import com.ttak.android.R

@Composable
fun ScreenTimeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. 앱 사용 약속 시간 표시
        PromiseAppTimeComponent(appIconResId = R.drawable.app_icon, startTime = "18:00", endTime = "20:00")

        Spacer(modifier = Modifier.height(16.dp))

        // 2. 사용 시간 변화 표시
        ScreenTimeChangeComponent(username = "remember the name", hoursDifference = -1)

        Spacer(modifier = Modifier.height(16.dp))

        // 3. 오늘 사용 시간 도넛 차트
        TodayAppUsageChartComponent()

        Spacer(modifier = Modifier.height(16.dp))

        // 4. 기간별 사용 시간 요약
        TimeUsageSummaryComponent(monthUsage = "120시간", weekUsage = "30시간", todayUsage = "4시간")

        Spacer(modifier = Modifier.height(16.dp))

        // 5. 요일별 스마트폰 사용량 막대 차트
        WeeklyUsageBarChartComponent()
    }
}
