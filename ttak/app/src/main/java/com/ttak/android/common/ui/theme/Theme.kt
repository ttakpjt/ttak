package com.ttak.android.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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
    val context = LocalContext.current
    val typography = getScaledTypography(context)

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = typography,
        content = content
    )
}
