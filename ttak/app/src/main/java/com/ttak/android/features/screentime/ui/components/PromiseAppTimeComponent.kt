package com.ttak.android.features.screentime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource

@Composable
fun PromiseAppTimeComponent(appIconResId: Int, startTime: String, endTime: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text = "⛔ $startTime ~ $endTime", fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = painterResource(id = appIconResId),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}