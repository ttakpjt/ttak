package com.ttak.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ttak.android.common.ui.theme.TtakTheme
import androidx.compose.ui.graphics.Color
import com.ttak.android.common.ui.components.AppButton
import com.ttak.android.common.ui.components.AppSearchBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // TtakTheme으로 앱 전체 UI를 감쌈
            TtakTheme {
                AppSearchBar(
                )
            }
        }
    }
}