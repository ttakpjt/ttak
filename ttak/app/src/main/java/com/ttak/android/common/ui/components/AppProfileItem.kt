package com.ttak.android.common.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttak.android.R
import com.ttak.android.common.ui.theme.White

@Composable
fun AppProfileItem(
    profileImage: Int? = null,       // 프로필 이미지 리소스 ID
    nickname: String,                // 사용자 이름
    borderColor: Color = Color.Black // 기본 테두리 색상
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally  // 수직 정렬
    ) {
        // 프로필 이미지
        Image(
            painter = painterResource(id = profileImage ?: R.drawable.default_proflie),  // 기본 이미지 사용
            contentDescription = "프로필 사진",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)  // 프로필 사진 크기
                .clip(CircleShape)
                .border(3.dp, borderColor, CircleShape)  // 테두리 추가
                .padding(2.dp)  // 테두리와 프로필 이미지 사이 간격
        )

        Spacer(modifier = Modifier.height(12.dp))  // 이미지와 이름 간격

        // 사용자 이름
        Text(
            text = nickname,
            style = MaterialTheme.typography.bodyLarge,
            color = White,
            fontSize = 18.sp
        )
    }
}
