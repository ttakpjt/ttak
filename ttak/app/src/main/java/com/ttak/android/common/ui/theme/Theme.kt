package com.ttak.android.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ttak.android.common.ui.theme.Black
import com.ttak.android.common.ui.theme.Grey
import com.ttak.android.common.ui.theme.Blue
import com.ttak.android.common.ui.theme.White
import com.ttak.android.common.ui.theme.Green
import com.ttak.android.common.ui.theme.Typography

// 다크 모드 컬러 설정
private val DarkColorScheme = darkColorScheme(
    primary = Grey,
    secondary = Red,
    tertiary = Blue,
    background = Black,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = Yellow
)

// 라이트 모드 컬러 설정
private val LightColorScheme = lightColorScheme(
    primary = Grey,
    secondary = Red,
    tertiary = Blue,
    background = Black,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = Yellow
)

@Composable
fun TtakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
