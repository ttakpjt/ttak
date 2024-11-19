package com.ttak.android.common.ui.components

import androidx.annotation.DrawableRes
import com.ttak.android.common.navigation.AppScreens

data class BottomNavItem(
    val label: String,
    @DrawableRes val icon: Int,
    val screen: AppScreens
)
