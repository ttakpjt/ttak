package com.ttak.android.features.observer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ttak.android.R
import com.ttak.android.domain.model.Time


fun formatTimeRange(startTime: Time, endTime: Time): String {
    return "${formatTime(startTime)} ~ ${formatTime(endTime)}"
}

private fun formatTime(time: Time): String {
    val hour = when {
        time.hour == 12 -> 12
        time.hour == 0 || time.hour == 24 -> 12
        time.hour > 12 -> time.hour - 12
        else -> time.hour
    }

    val period = if (time.hour >= 12 && time.hour < 24) "PM" else "AM"
    val minute = String.format("%02d", time.minute)  // 분을 두 자리 숫자로 표시 (예: 05)

    return "$hour:${minute}$period"
}

private fun timeToMinutes(time: Time): Int {
    return time.hour * 60 + time.minute
}

@Composable
fun TimeProgress(
    startTime: Time,
    endTime: Time,
    currentTime: Time
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.prohibition_icon),
                contentDescription = "Friends Icon",
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = formatTimeRange(startTime, endTime),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))  // 간격 조정

        val startMinutes = timeToMinutes(startTime)
        val endMinutes = timeToMinutes(endTime)
        val currentMinutes = timeToMinutes(currentTime)

        val progress = (currentMinutes - startMinutes).toFloat() / (endMinutes - startMinutes).toFloat()

        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = Color(0xFFFF4C3E),
            trackColor = Color(0xFFEAEAEA)
        )
    }
}