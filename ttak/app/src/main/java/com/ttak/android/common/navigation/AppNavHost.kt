package com.ttak.android.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ttak.android.features.auth.ui.screens.SplashScreen
import com.ttak.android.features.auth.ui.screens.AuthScreen
import com.ttak.android.features.history.ui.screens.HistoryScreen
import com.ttak.android.features.mypage.ui.screens.MyPageScreen
import com.ttak.android.features.observer.ui.screens.ObserverScreen
import com.ttak.android.features.screentime.ui.screens.ScreenTimeScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.Home.route
    ) {
        composable(AppScreens.Home.route) { SplashScreen(navController) }
        composable(AppScreens.Login.route) { AuthScreen(navController) }
        composable(AppScreens.History.route) { HistoryScreen(navController) }
        composable(AppScreens.MyPage.route) { MyPageScreen(navController) }
//        composable(AppScreens.Observer.route) { ObserverScreen(navController) }
        composable(AppScreens.ScreenTime.route) { ScreenTimeScreen(navController) }
    }
}
