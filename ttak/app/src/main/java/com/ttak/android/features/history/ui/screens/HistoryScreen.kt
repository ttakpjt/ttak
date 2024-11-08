package com.ttak.android.features.history.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttak.android.features.history.ui.components.HistoryMessageItem
import com.ttak.android.features.history.ui.components.SystemNotificationCard
import com.ttak.android.features.history.viewmodel.HistoryViewModel
import com.ttak.android.features.history.viewmodel.HistoryViewModelFactory
import com.ttak.android.network.api.PreviewHistoryApi

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory(PreviewHistoryApi())
    )
) {
    val messages by viewModel.messages.collectAsState()
    val systemNotification by viewModel.systemNotification.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E181E))
    ) {
        SystemNotificationCard(
            notificationText = "주간 딱걸림\n10번",
            watchingCount = 5
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages) { message ->
                HistoryMessageItem(message = message)
            }
        }
    }
}