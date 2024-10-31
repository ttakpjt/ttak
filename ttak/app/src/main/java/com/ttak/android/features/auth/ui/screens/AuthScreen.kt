package com.ttak.android.features.auth.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ttak.android.common.navigation.NavigationManager
import com.ttak.android.common.navigation.AppScreens

@Composable
fun AuthScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 간단한 로그인 메시지
        Text(text = "로그인 화면")

        Spacer(modifier = Modifier.height(16.dp))

        // 버튼을 눌러 다른 화면으로 이동
        Button(onClick = {
            NavigationManager.navigateTo(AppScreens.History)  // 예시로 History 화면으로 이동
        }) {
            Text(text = "로그인 후 이동")
        }
    }
}
