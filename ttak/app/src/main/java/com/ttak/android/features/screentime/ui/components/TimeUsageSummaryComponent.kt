package com.ttak.android.features.screentime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttak.android.common.ui.theme.Yellow
import com.ttak.android.common.ui.theme.Black
import com.ttak.android.common.ui.theme.Red
import com.ttak.android.utils.formatDuration

@Composable
fun TimeUsageSummaryComponent(monthUsage: Int, weekUsage: Int, todayUsage: Int) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(shape = MaterialTheme.shapes.medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Yellow)
                .fillMaxWidth()
                .padding(16.dp) // 내부 여백
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "이번 달",
                    style = MaterialTheme.typography.bodySmall,
                    color = Black,
//                    fontSize = 16.sp
                )
                Text(text = formatDuration(monthUsage), style = MaterialTheme.typography.bodyLarge, color = Black)
            }

            Spacer(
                modifier = Modifier
                    .width(2.dp)
                    .height(48.dp) // 텍스트 높이에 맞춘 값 설정
                    .background(Black)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "지난 주",
                    style = MaterialTheme.typography.bodySmall,
                    color = Black,
//                    fontSize = 16.sp
                )
                Text(text = formatDuration(weekUsage), style = MaterialTheme.typography.bodyLarge, color = Black)
            }

            Spacer(
                modifier = Modifier
                    .width(2.dp)
                    .height(48.dp) // 텍스트 높이에 맞춘 값 설정
                    .background(Black)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "오늘",
                    style = MaterialTheme.typography.bodySmall,
                    color = Black,
//                    fontSize = 16.sp
                )
                Text(text = formatDuration(todayUsage), style = MaterialTheme.typography.bodyLarge, color = Red)
            }
        }
    }
}
