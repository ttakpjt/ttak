package com.ttak.android.features.observer.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.ttak.android.R
import com.ttak.android.domain.model.GoalState

@Composable
fun SetGoalCard(
    goalState: GoalState
) {
    // 로그 추가
    Log.d("SetGoalCard", "GoalState: isSet=${goalState.isSet}")
    Log.d("SetGoalCard", "Observer Count: ${goalState.observerCount}")
    Log.d("SetGoalCard", "Selected Apps: ${goalState.selectedApps.map { it.appName }}")
    Log.d("SetGoalCard", "Start Time: ${goalState.startTime.hour}:${goalState.startTime.minute}")
    Log.d("SetGoalCard", "End Time: ${goalState.endTime.hour}:${goalState.endTime.minute}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.friends_icon),
                contentDescription = "Friends Icon",
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            // 이규석
            Text(
                text = "${goalState.observerCount}명이 지켜보고 있어요",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                maxLines = 1,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 선택된 앱 아이콘들을 가로로 표시
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()) // 앱이 많을 경우 스크롤 가능하도록
        ) {
            goalState.selectedApps.forEach { app ->
                Image(
                    painter = rememberDrawablePainter(drawable = app.icon),
                    contentDescription = "App Icon - ${app.appName}",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        TimeProgress(
            startTime = goalState.startTime,
            endTime = goalState.endTime,
        )
    }
}