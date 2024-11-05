package com.ttak.android.features.goal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
        // Time selector
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

        // App list header
        Text(
            text = "앱 선택",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // App list
        LazyColumn {
            items(installedApps) { app ->
                AppSelectItem(
                    app = app,
                    isSelected = selectedApps.contains(app.packageName),
                    onSelectApp = { viewModel.toggleAppSelection(app.packageName) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save button
        Button(
            onClick = {
                viewModel.saveGoal()
                onNavigateBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("저장하기")
        }
    }
}