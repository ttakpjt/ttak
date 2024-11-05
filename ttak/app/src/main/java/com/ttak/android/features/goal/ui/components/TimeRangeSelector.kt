package com.ttak.android.features.goal.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TimeRangeSelector(
    startTime: LocalTime,
    endTime: LocalTime,
    onStartTimeSelected: (Int, Int) -> Unit,
    onEndTimeSelected: (Int, Int) -> Unit
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimePickerButton(
            time = startTime,
            onTimeSelected = onStartTimeSelected,
            timeFormatter = timeFormatter,
            label = "시작 시간"
        )

        Text("~")

        TimePickerButton(
            time = endTime,
            onTimeSelected = onEndTimeSelected,
            timeFormatter = timeFormatter,
            label = "종료 시간"
        )
    }
}