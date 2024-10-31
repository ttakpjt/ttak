package com.ttak.android.features.observer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier  // modifier 파라미터 추가
) {
    Row(
        modifier = modifier.padding(bottom = 16.dp),  // 기존 modifier에 추가
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { iteration ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentPage == iteration) {
                            Color(0xFF8CD5FE)
                        } else {
                            Color(0xFFEDEDED)
                        }
                    )
                    .then(
                        if (currentPage == iteration) {
                            Modifier.size(width = 20.dp, height = 8.dp)
                        } else {
                            Modifier.size(8.dp)
                        }
                    )
            )
        }
    }
}