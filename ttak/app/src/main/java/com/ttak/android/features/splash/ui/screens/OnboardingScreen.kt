package com.ttak.android.features.splash.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ttak.android.common.ui.theme.White

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("앱 사용 설명 1", style = MaterialTheme.typography.bodyLarge)
            Text("앱 사용 설명 2")
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onComplete) {
                Text("시작하기")
            }
        }
    }
}