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
    fun navigateTo(route: String, navOptions: NavOptions? = null) {
        navController?.navigate(route, navOptions)
    }

    // 뒤로가기 기능
    fun navigateBack() {
        navController?.popBackStack()
    }

    // 특정 루트로 이동 (스택 초기화)
    fun navigateAndClearStack(route: String) {
        navController?.navigate(route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }
}
