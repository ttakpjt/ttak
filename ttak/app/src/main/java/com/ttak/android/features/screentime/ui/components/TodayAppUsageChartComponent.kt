package com.ttak.android.features.screentime.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ttak.android.utils.formatDuration
import com.ttak.android.utils.getAppNameFromPackageName

@Composable
fun TodayAppUsageChartComponent(topSixAppsUsage: Map<String, Int>) {
    val context = LocalContext.current
    val totalUsage = topSixAppsUsage.values.sum()
    val appColors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        // 도넛 차트
        Box(modifier = Modifier.size(100.dp)) {
            Canvas(modifier = Modifier.size(100.dp)) {
                var startAngle = -90f
                topSixAppsUsage.values.forEachIndexed { index, usage ->
                    val sweepAngle = if (totalUsage > 0) (usage.toFloat() / totalUsage) * 360f else 0f
                    drawArc(
                        color = appColors.getOrElse(index) { Color.Gray },
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 20.dp.toPx())
                    )
                    startAngle += sweepAngle
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 범례
        Column {
            topSixAppsUsage.entries.forEachIndexed { index, (packageName, usage) ->
                val appName = getAppNameFromPackageName(context, packageName)
                val color = appColors.getOrElse(index) { Color.Gray }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "$appName: ${formatDuration(usage)}")
                }
            }
        }
    }
}
