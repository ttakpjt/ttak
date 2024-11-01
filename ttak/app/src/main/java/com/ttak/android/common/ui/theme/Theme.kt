package com.ttak.android.common.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ttak.android.common.ui.theme.Black
import com.ttak.android.common.ui.theme.Grey
import com.ttak.android.common.ui.theme.Blue
import com.ttak.android.common.ui.theme.White
import com.ttak.android.common.ui.theme.Typography

// 다크 모드 컬러 설정
private val DarkColorScheme = darkColorScheme(
    primary = Black,
    secondary = Grey,
    tertiary = Blue,
    background = Blue,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = White
)

// 라이트 모드 컬러 설정
private val LightColorScheme = lightColorScheme(
    primary = Black,
    secondary = Grey,
    tertiary = Blue,
    background = Blue,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = White
)

@Composable
fun TtakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography.copy(bodyLarge = Typography.bodyLarge),
        content = content
    )
}
