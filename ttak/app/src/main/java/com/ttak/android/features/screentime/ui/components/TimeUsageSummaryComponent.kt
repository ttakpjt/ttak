package com.ttak.android.features.screentime.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimeUsageSummaryComponent(monthUsage: String, weekUsage: String, todayUsage: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text(text = "이번 달", fontSize = 14.sp)
            Text(text = monthUsage, fontSize = 20.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text(text = "지난 주", fontSize = 14.sp)
            Text(text = weekUsage, fontSize = 20.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text(text = "오늘", fontSize = 14.sp)
            Text(text = todayUsage, fontSize = 20.sp)
        }
    }
}