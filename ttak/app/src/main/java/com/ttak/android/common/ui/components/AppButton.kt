package com.ttak.android.common.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,  // 버튼 배경색
    contentColor: Color = Color.White  // 텍스트 색상
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(160.dp)
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor, contentColor = contentColor),
        shape = RoundedCornerShape(24.dp)  // 모서리 기울기
    ) {
        Text(text = text, color = contentColor, style = MaterialTheme.typography.labelLarge)  // 텍스트 색상, 스타일 정의
    }
}