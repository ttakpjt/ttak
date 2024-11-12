package com.ttak.android.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ttak.android.features.history.ui.screens.HistoryScreen
import com.ttak.android.features.mypage.ui.screens.MyPageScreen
import com.ttak.android.features.observer.ui.screens.ObserverScreen
import com.ttak.android.features.screentime.ui.screens.ScreenTimeScreen
import com.ttak.android.features.goal.ui.screens.SetGoalScreen
import com.ttak.android.features.observer.viewmodel.FriendStoryViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    friendStoryViewModel: FriendStoryViewModel
) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.ScreenTime.route  // 앱 시작점은 스크린타임 화면으로
    ) {
        composable(AppScreens.History.route) { HistoryScreen() }
        composable(AppScreens.MyPage.route) { MyPageScreen() }
        composable(AppScreens.Observer.route) {
            ObserverScreen(friendStoryViewModel = friendStoryViewModel)  // ViewModel 전달
        }
        composable(AppScreens.ScreenTime.route) { ScreenTimeScreen() }
        composable(AppScreens.SetGoal.route) {
            SetGoalScreen(
                onNavigateBack = {
                    NavigationManager.navigateBack()
                }
            )
        }
    }
}
