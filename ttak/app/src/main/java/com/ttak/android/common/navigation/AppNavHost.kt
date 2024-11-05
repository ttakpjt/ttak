package com.ttak.android.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ttak.android.features.history.ui.screens.HistoryScreen
import com.ttak.android.features.mypage.ui.screens.MyPageScreen
import com.ttak.android.features.observer.ui.screens.ObserverScreen
//import com.ttak.android.features.screentime.ui.screens.ScreenTimeScreen
import com.ttak.android.features.goal.ui.screens.SetGoalScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.History.route
    ) {
        composable(AppScreens.History.route) { HistoryScreen() }
        composable(AppScreens.MyPage.route) { MyPageScreen() }
        composable(AppScreens.Observer.route) { ObserverScreen() }
//        composable(AppScreens.ScreenTime.route) { ScreenTimeScreen() }
        composable(AppScreens.SetGoal.route) {
            SetGoalScreen(
                onNavigateBack = {
                    NavigationManager.navigateBack()
                }
            )
        }
    }
}
