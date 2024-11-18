package com.ttak.android.features.observer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ttak.android.R
import com.ttak.android.domain.model.FilterOption

@Composable
fun FriendListFilter(
    options: List<FilterOption>,
    selectedOptionId: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // 현재 선택된 옵션 찾기
    val currentOption = options.find { it.id == selectedOptionId } ?: options.firstOrNull()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        currentOption?.let { option ->
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        // 전체(1)와 감지(2) 사이를 토글
                        val nextId = if (option.id == 1) 2 else 1
                        onOptionSelected(nextId)
                    },
                color = if (option.id == 1) Color(0xFF451f7f) else Color(0xFFFF5E5E)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 필터 아이콘 추가
                    Icon(
                        painter = painterResource(id = R.drawable.filter_icon),
                        contentDescription = "Filter",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option.title,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
//                        fontWeight = FontWeight.Bold
//                        fontSize = 20.sp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color.Black,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option.count.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}