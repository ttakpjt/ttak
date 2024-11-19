package com.ttak.android.features.goal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttak.android.R
import com.ttak.android.features.goal.ui.components.AppSelectItem
import com.ttak.android.features.goal.ui.components.TimeRangeSelector
import com.ttak.android.features.goal.viewmodel.SetGoalViewModel
import kotlinx.coroutines.launch

@Composable
fun SetGoalScreen(
    onNavigateBack: () -> Unit,
    onNavigateToObserver: () -> Unit
) {
    val viewModel: SetGoalViewModel = viewModel()
    val startTime by viewModel.startTime.collectAsState()
    val endTime by viewModel.endTime.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val selectedApps by viewModel.selectedApps.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(saveSuccess) {
        if (saveSuccess == true) {
            onNavigateToObserver()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Gradient Background
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E1065),
                        Color(0xFF1E1B4B),
                        Color(0xFF172554)
                    )
                )
            )
        )

        // Decorative Blurred Circles
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 150.dp, y = (-50).dp)
                .background(Color(0x33A855F7), CircleShape)
                .blur(radius = 70.dp)
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-50).dp, y = 150.dp)
                .background(Color(0x333B82F6), CircleShape)
                .blur(radius = 70.dp)
        )

        // Main Content
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
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "제한시간 설정",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
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
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "앱 선택",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // App list with semi-transparent background
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        Color(0x1AFFFFFF),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(8.dp)
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
                        containerColor = Color(0x33FFFFFF),
                        contentColor = Color.White
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
                        containerColor = Color(0xFF3B82F6),
                        contentColor = Color.White
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "등록하기",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}