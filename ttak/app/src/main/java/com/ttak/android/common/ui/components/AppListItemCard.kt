package com.ttak.android.common.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttak.android.R
import androidx.compose.ui.graphics.Color
import com.ttak.android.common.ui.theme.White

@Composable
fun AppListItemCard(
    profileImage: Int? = null,  // 왼쪽 이미지 리소스 ID
    title: String,               // 중앙 텍스트 제목
    optionContent: @Composable (() -> Unit)? = null  // 오른쪽 옵션 컴포저블 (아이콘, 버튼 등)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),  // x축 간격 설정
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(Color(0xFF2F2F32))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽 이미지
            Image(
                painter = painterResource(id = profileImage ?: R.drawable.default_proflie),
                contentDescription = "사진",
                modifier = Modifier
                    .size(72.dp)
                    .padding(end = 16.dp)
            )

            // 중앙 텍스트 영역
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,  // 텍스트 스타일 적용
                fontSize = 20.sp,
                color = White
            )

            // 오른쪽 옵션 영역
            Spacer(modifier = Modifier.weight(1f))
            optionContent?.invoke()
        }
    }
}
