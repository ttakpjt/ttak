package com.ttak.android.features.screentime.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WeeklyUsageBarChartComponent() {
    Column(modifier = Modifier.padding(8.dp)) {
        Text("요일별 사용량 (막대 차트 자리)")
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            // 막대 차트 라이브러리를 사용해 각 요일의 사용량을 구현할 부분
            Text("월")
            Text("화")
            Text("수")
            Text("목")
            Text("금") // 오늘일 경우, 색상을 빨간색으로
            Text("토")
            Text("일")
        }
    }
}