package com.ttak.android.features.observer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.ttak.android.domain.model.CountResponse

@Composable
fun Dashboard(
    countData: CountResponse,   // totalCount, myCount, friendsCount
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            GaugeComponent(title = "사용자", total = countData.data.totalCount, my = countData.data.myCount)
            Spacer(modifier = Modifier.height(32.dp))
            GaugeComponent(title = "친구", total = countData.data.friendsCount, my = countData.data.myCount)
        }
    }
}

@Composable
fun GaugeComponent(title: String, total: Int, my: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)

        Canvas(
            modifier = Modifier
                .size(120.dp)
                .padding(16.dp)
        ) {
            // 반원형 게이지 배경 그리기
            drawArc(
                color = Color.Gray,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx())
            )

            // 바늘 각도 계산 (부동 소수점으로 계산)
            val percentage = (my.toFloat() / total.toFloat()) * 100
            val needleAngle = 270f + percentage * 180f / 100

            // 바늘 그리기
            rotate(degrees = needleAngle) {
                drawLine(
                    color = Color.Red,
                    start = center,
                    end = center.copy(y = center.y - size.minDimension / 2),
                    strokeWidth = 4.dp.toPx()
                )
            }
        }

        // 적발 횟수 비교 텍스트
        Text(text = "전체 ${total}번 중 ${my}번", style = MaterialTheme.typography.bodySmall)
    }
}
