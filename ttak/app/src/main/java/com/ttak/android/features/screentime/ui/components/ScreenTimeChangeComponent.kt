package com.ttak.android.features.screentime.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ttak.android.utils.formatDuration
import com.ttak.android.common.ui.theme.Red
import com.ttak.android.common.ui.theme.Green
import kotlin.math.absoluteValue


@Composable
fun ScreenTimeChangeComponent(username: String, hoursDifference: Int) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "${username}님", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = buildAnnotatedString {
                append("어제보다 ")
                withStyle(style = SpanStyle(color = if (hoursDifference >= 0) Red else Green)) {
                    append(formatDuration(hoursDifference.absoluteValue))
                }
                append(if (hoursDifference >= 0) " 더" else " 덜")
                append(" 보셨네요!")
            },
            style = MaterialTheme.typography.titleSmall
        )
    }
}