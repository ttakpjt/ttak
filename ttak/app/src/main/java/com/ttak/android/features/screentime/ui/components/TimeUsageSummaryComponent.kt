package com.ttak.android.features.screentime.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttak.android.utils.formatDuration

@Composable
fun TimeUsageSummaryComponent(monthUsage: Int, weekUsage: Int, todayUsage: Int) {
    Log.d("이규석", todayUsage.toString())
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text(text = "이번 달", fontSize = 14.sp)
            Text(text = formatDuration(monthUsage), fontSize = 20.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text(text = "지난 주", fontSize = 14.sp)
            Text(text = formatDuration(weekUsage), fontSize = 20.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text(text = "오늘", fontSize = 14.sp)
            Text(text = formatDuration(todayUsage), fontSize = 20.sp)
        }
    }
}