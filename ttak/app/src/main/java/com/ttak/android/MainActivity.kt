package com.ttak.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ttak.android.common.ui.theme.TtakTheme

import com.ttak.android.common.ui.components.AppButton
import com.ttak.android.common.ui.components.AppSearchBar
import com.ttak.android.common.ui.components.AppProfileItem
import com.ttak.android.common.ui.theme.Green

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // TtakTheme으로 앱 전체 UI를 감쌈
            TtakTheme {
                AppProfileItem(
                    nickName = "벨랄 무하마드",
                    borderColor = Green
                )
            }
        }
    }
}