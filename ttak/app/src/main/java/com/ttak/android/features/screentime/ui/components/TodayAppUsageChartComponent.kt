package com.ttak.android.features.screentime.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TodayAppUsageChartComponent() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        // 차트(도넛 차트)
        Box(modifier = Modifier.size(100.dp)) {
            // 차트 라이브러리를 사용해 구현할 부분
            Text(text = "도넛 차트 자리")
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 범례
        Column {
            Text("Chrome: 1시간")
            Text("Instagram: 2시간")
            Text("YouTube: 1시간 30분")
        }
    }
}