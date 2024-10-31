package com.ttak.android.features.observer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ttak.android.features.observer.ui.components.CardCarousel
import com.ttak.android.data.model.GoalState

@Composable
fun ObserverScreen(
    goalState: GoalState = GoalState()
) {
    CardCarousel(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        goalState = goalState
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ObserverScreenPreview() {
    ObserverScreen()
}