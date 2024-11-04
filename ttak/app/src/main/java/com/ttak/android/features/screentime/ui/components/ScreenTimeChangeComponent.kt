package com.ttak.android.features.screentime.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttak.android.utils.formatDuration
import kotlin.math.absoluteValue

@Composable
fun ScreenTimeChangeComponent(username: String, hoursDifference: Int) {
    Column(modifier = Modifier.padding(8.dp)) {
        // 추후 닉네임으로 변경
        Text(text = "이규석님!", fontSize = 18.sp)
        // hoursDifference을 통해 사용 시간 계산
        Text(
            text =
            "어제보다 ${if (hoursDifference >= 0) formatDuration(hoursDifference) + " 더" 
            else formatDuration(hoursDifference.absoluteValue) + " 덜" } 보셨네요!",
            fontSize = 14.sp
        )
    }
}