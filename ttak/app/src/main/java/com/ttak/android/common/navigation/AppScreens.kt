package com.ttak.android.common.navigation

sealed class AppScreens(val route: String) {
    object Home : AppScreens("home")
    object Login : AppScreens("login")
    object History : AppScreens("history")
    object MyPage : AppScreens("my_page")
    object Observer : AppScreens("observer")
    object ScreenTime : AppScreens("screen_time")
}