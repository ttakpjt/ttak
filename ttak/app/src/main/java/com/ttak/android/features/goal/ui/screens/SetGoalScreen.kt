package com.ttak.android.features.goal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttak.android.R
import com.ttak.android.features.goal.ui.components.AppSelectItem
import com.ttak.android.features.goal.ui.components.TimeRangeSelector
import com.ttak.android.features.goal.viewmodel.SetGoalViewModel

@Composable
fun SetGoalScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel: SetGoalViewModel = viewModel()
    val startTime by viewModel.startTime.collectAsState()
    val endTime by viewModel.endTime.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val selectedApps by viewModel.selectedApps.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Time selector with icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.time_icon),
                contentDescription = "시간 설정",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "제한시간 설정",
                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold
            )
        }

        TimeRangeSelector(
            startTime = startTime,
            endTime = endTime,
            onStartTimeSelected = { hour, minute ->
                viewModel.updateStartTime(hour, minute)
            },
            onEndTimeSelected = { hour, minute ->
                viewModel.updateEndTime(hour, minute)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // App list header with icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.apps_icon),
                contentDescription = "앱 선택",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "앱 선택",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
//                fontWeight = FontWeight.Bold
            )
        }

        // App list with weight modifier
        LazyColumn(
            modifier = Modifier
                .weight(1f)  // 남은 공간의 비율 설정
                .fillMaxWidth()
        ) {
            items(installedApps) { app ->
                AppSelectItem(
                    app = app,
                    isSelected = selectedApps.contains(app.packageName),
                    onSelectApp = { viewModel.toggleAppSelection(app.packageName) }
                )
            }
        }

        // Buttons row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 취소하기 버튼
            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB2B2B4),
                    contentColor = Color.Black  // 텍스트 색상을 검은색으로 설정
                )
            ) {
                Text(
                    text = "취소하기",
                    style = MaterialTheme.typography.titleSmall,
//                    fontWeight = FontWeight.ExtraBold,  // 더 진한 볼드체
//                    fontSize = 20.sp,  // 폰트 크기 증가
//                    color = Color.Black  // 텍스트 색상 명시적 지정
                )
            }

            // 등록하기 버튼
            Button(
                onClick = {
                    viewModel.saveGoal()
                    onNavigateBack()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8CD5FE),
                    contentColor = Color.Black  // 텍스트 색상을 검은색으로 설정
                )
            ) {
                Text(
                    text = "등록하기",
                    style = MaterialTheme.typography.titleSmall,
//                    fontWeight = FontWeight.ExtraBold,  // 더 진한 볼드체
//                    fontSize = 20.sp,  // 폰트 크기 증가
                    color = Color.Black  // 텍스트 색상 명시적 지정
                )
            }
        }
    }
}