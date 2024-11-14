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
import kotlinx.coroutines.launch

@Composable
fun SetGoalScreen(
    onNavigateBack: () -> Unit,
    onNavigateToObserver: () -> Unit  // 새로운 네비게이션 콜백 추가
) {
    val viewModel: SetGoalViewModel = viewModel()
    val startTime by viewModel.startTime.collectAsState()
    val endTime by viewModel.endTime.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val selectedApps by viewModel.selectedApps.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val scope = rememberCoroutineScope()

    // saveSuccess 상태 관찰
    LaunchedEffect(saveSuccess) {
        if (saveSuccess == true) {
            onNavigateToObserver()
        }
    }

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
                style = MaterialTheme.typography.titleMedium
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
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // App list with weight modifier
        LazyColumn(
            modifier = Modifier
                .weight(1f)
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
            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB2B2B4),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "취소하기",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            var isLoading by remember { mutableStateOf(false) }

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        try {
                            viewModel.saveGoal()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8CD5FE),
                    contentColor = Color.Black
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black
                    )
                } else {
                    Text(
                        text = "등록하기",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black
                    )
                }
            }
        }
    }
}