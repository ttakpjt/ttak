package com.ttak.android.features.goal.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun TimePickerButton(
    time: LocalTime,
    onTimeSelected: (Int, Int) -> Unit,
    timeFormatter: DateTimeFormatter,
    label: String,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDialog) {
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hour, minute ->
                onTimeSelected(hour, minute)
                showDialog = false
            },
            time.hour,
            time.minute,
            true // 24시간 형식 사용
        )

        DisposableEffect(Unit) {
            timePickerDialog.show()
            onDispose {
                timePickerDialog.dismiss()
            }
        }
    }

    androidx.compose.material3.Surface(
        modifier = modifier
            .padding(4.dp)
            .clickable { showDialog = true },
        shape = MaterialTheme.shapes.medium,
        color = Color(0xFF2F2F32)
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White  // 흰색으로 변경
            )

            Text(
                text = time.format(timeFormatter),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,  // 흰색으로 변경
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}