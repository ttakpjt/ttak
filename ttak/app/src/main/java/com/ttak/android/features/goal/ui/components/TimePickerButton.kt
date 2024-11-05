package com.ttak.android.features.goal.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TimePickerButton(
    time: LocalTime,
    onTimeSelected: (Int, Int) -> Unit,
    timeFormatter: DateTimeFormatter,
    label: String
) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = time.format(timeFormatter),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.clickable {
                TimePickerDialog(
                    context,
                    { _, hour, minute -> onTimeSelected(hour, minute) },
                    time.hour,
                    time.minute,
                    true
                ).show()
            }
        )
    }
}