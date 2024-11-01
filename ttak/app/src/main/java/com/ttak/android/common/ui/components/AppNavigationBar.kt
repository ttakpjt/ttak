package com.ttak.android.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ttak.android.R
import com.ttak.android.common.navigation.AppScreens
import com.ttak.android.common.ui.theme.Black
import com.ttak.android.common.ui.theme.Grey
import com.ttak.android.common.ui.theme.Red

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("observer", R.drawable.account_multiple, AppScreens.Observer),
        BottomNavItem("screen_time", R.drawable.home, AppScreens.ScreenTime),
        BottomNavItem("history", R.drawable.bomb, AppScreens.History)
    )

    // 하단 네비게이션 바 설정
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // 현재 선택된 경로
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

            items.forEach { item ->
                val isSelected = currentRoute == item.screen.route
                BottomNavItem(
                    item = item,
                    isSelected = isSelected,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .size(width = 82.dp, height = 48.dp)
            .padding(8.dp)
//            .background(if (isSelected) Color(0xFF515155) else Color.Transparent, shape = CircleShape)
            .clickable {
                navController.navigate(item.screen.route) {
                    popUpTo(AppScreens.Home.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
            .pointerInput(Unit) {},  // 호버 이벤트 무시
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.label,
            tint = Grey,
            modifier = Modifier.size(32.dp)
        )
    }
}
