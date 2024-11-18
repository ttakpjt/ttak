package com.ttak.android.features.screentime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
                .background(Color(0xFF1E1B4B)) // Deep indigo background
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "이번 달",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF8B7EF3) // Soft lavender
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatDuration(monthUsage),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }

            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .height(48.dp)
                    .background(Color(0xFF4338CA)) // Slightly lighter indigo for divider
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "지난 주",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF8B7EF3) // Soft lavender
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatDuration(weekUsage),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }

            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .height(48.dp)
                    .background(Color(0xFF4338CA)) // Slightly lighter indigo for divider
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "오늘",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF8B7EF3)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatDuration(todayUsage),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFEF4444)
                )
            }
        }
    }
}