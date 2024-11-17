package com.ttak.android.features.observer.ui.components

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.ui.unit.sp
import com.ttak.android.R
import com.ttak.android.domain.model.Time
import java.util.Calendar


fun formatTimeRange(startTime: Time, endTime: Time): String {
    return "${formatTime(startTime)} ~ ${formatTime(endTime)}"
}

@SuppressLint("DefaultLocale")
private fun formatTime(time: Time): String {
    val hour = when {
        time.hour == 12 -> 12
        time.hour == 0 || time.hour == 24 -> 12
        time.hour > 12 -> time.hour - 12
        else -> time.hour
    }

    val period = if (time.hour in 12..23) "PM" else "AM"
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
    currentTime: Time = getCurrentTime()
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
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = formatTimeRange(startTime, endTime),
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // calculateProgress 함수 사용
        val progress = calculateProgress(startTime, endTime, currentTime)

        Log.d("TimeProgress", "Progress: $progress")
        Log.d("TimeProgress", "Start Time: ${formatTime(startTime)}")
        Log.d("TimeProgress", "End Time: ${formatTime(endTime)}")
        Log.d("TimeProgress", "Current Time: ${formatTime(currentTime)}")

        LinearProgressIndicator(
            progress = {
                progress
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(8.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = Color(0xFFFF4C3E),
            trackColor = Color(0xFFEAEAEA),
        )
    }
}

private fun calculateProgress(start: Time, end: Time, current: Time): Float {
    // 모든 시간을 분으로 변환
    val startMinutes = start.hour * 60 + start.minute
    val endMinutes = end.hour * 60 + end.minute
    val currentMinutes = current.hour * 60 + current.minute

    // 전체 기간 계산
    val totalMinutes = if (endMinutes > startMinutes) {
        endMinutes - startMinutes
    } else {
        (24 * 60) - startMinutes + endMinutes
    }

    // 경과 시간 계산
    val elapsedMinutes = when {
        // 현재 시간이 시작 시간과 종료 시간 사이에 있는 경우
        currentMinutes in startMinutes..endMinutes ->
            currentMinutes - startMinutes
        // 자정을 넘어가는 경우
        endMinutes < startMinutes && (currentMinutes >= startMinutes || currentMinutes <= endMinutes) ->
            if (currentMinutes >= startMinutes)
                currentMinutes - startMinutes
            else
                (24 * 60 - startMinutes) + currentMinutes
        // 그 외의 경우
        else -> 0
    }

    Log.d("TimeProgress", """
        계산 결과:
        전체 기간: $totalMinutes 분
        경과 시간: $elapsedMinutes 분
        진행률: ${(elapsedMinutes.toFloat() / totalMinutes)}
    """.trimIndent())

    return (elapsedMinutes.toFloat() / totalMinutes).coerceIn(0f, 1f)
}
fun getCurrentTime(): Time {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)  // HOUR_OF_DAY를 사용하여 24시간 형식으로 가져옴
    val minute = calendar.get(Calendar.MINUTE)

    Log.d("TimeProgress", "getCurrentTime: $hour:$minute")

    return Time(
        hour = hour,
        minute = minute
    )
}