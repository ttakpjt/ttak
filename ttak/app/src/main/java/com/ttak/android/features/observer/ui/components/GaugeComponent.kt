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
import androidx.compose.ui.unit.sp

@Composable
fun Dashboard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GaugeComponent(title = "전체 유저", percentage = 75)
        Spacer(modifier = Modifier.height(32.dp))
        GaugeComponent(title = "친구", percentage = 45)
    }
}

@Composable
fun GaugeComponent(title: String, percentage: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 20.sp)

        Canvas(
            modifier = Modifier
                .size(200.dp)
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

            // 바늘 각도 계산
            val needleAngle = 180f + (percentage / 100f) * 180f

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

        // 순위 퍼센트 텍스트
        Text(text = "상위 $percentage%", fontSize = 16.sp)
    }
}
