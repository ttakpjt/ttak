package com.ttak.android.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { /* Home Screen */ }
        composable("details") { /* Details Screen */ }
        // 다른 화면들도 이곳에 정의
    }
}
