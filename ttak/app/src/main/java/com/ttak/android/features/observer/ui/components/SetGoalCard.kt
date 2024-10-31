package com.ttak.android.features.observer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.ttak.android.R
import com.ttak.android.data.model.GoalState

@Composable
fun SetGoalCard(
    goalState: GoalState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally  // 전체 가운데 정렬
    ) {
        Row(  // Row로 감싸서 가로 배치
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center // 가로 방향 가운데 정렬 추가
//            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.friends_icon),
                contentDescription = "Friends Icon",
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "${goalState.observerCount}명이 지켜보고 있어요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 1,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "App Icon",
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        TimeProgress(
            startTime = goalState.startTime,
            endTime = goalState.endTime,
            currentTime = goalState.currentTime
        )
    }
}