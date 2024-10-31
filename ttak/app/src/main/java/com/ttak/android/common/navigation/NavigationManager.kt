package com.ttak.android.common.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptions

object NavigationManager {

    private var navController: NavHostController? = null

    // NavController를 초기화
    fun setNavController(controller: NavHostController) {
        navController = controller
    }

    // 화면 이동
    fun navigateTo(screen: AppScreens, navOptions: NavOptions? = null) {
        navController?.navigate(screen.route, navOptions)
    }

    // 뒤로가기 기능
    fun navigateBack() {
        navController?.popBackStack()
    }

    // 특정 루트로 이동 (스택 초기화)
    fun navigateAndClearStack(screen: AppScreens) {
        navController?.navigate(screen.route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }
}
