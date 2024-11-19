package com.ttak.android.features.splash.ui.screens

import android.widget.Button
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttak.android.R
import com.ttak.android.common.ui.theme.Black
import com.ttak.android.common.ui.theme.Blue
import com.ttak.android.common.ui.theme.Grey
import com.ttak.android.common.ui.theme.White
import com.ttak.android.common.ui.theme.Yellow

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val totalPages = 6
    var currentPage by remember { mutableStateOf(0) }
    var isDragging by remember { mutableStateOf(false) } // 슬라이드 중인지 상태 저장

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween // 요소 간에 균등하게 간격 확보
        ) {
            // "건너뛰기" 텍스트
            Text(
                text = "건너뛰기",
                style = MaterialTheme.typography.bodySmall,
                color = White,
                modifier = Modifier
                    .align(Alignment.End) // 오른쪽 끝 정렬
                    .clickable(onClick = onComplete)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp)) // 추가적인 여백 확보

            // 메인 콘텐츠
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 이미지 슬라이더
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f) // 화면 너비의 85%로 설정
                        .fillMaxHeight(0.8f) // 화면 높이의 80%로 설정
                        .clip(RoundedCornerShape(16.dp))
                        // 슬라이드 했을 때 넘기는 로직
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = {
                                    if (!isDragging) isDragging = true // 드래그 시작
                                },
                                onDragEnd = {
                                    isDragging = false // 드래그 종료
                                },
                                onHorizontalDrag = { _, dragAmount ->
                                    if (isDragging) {
                                        if (dragAmount < -50 && currentPage < totalPages - 1) {
                                            currentPage++
                                            isDragging = false // 한 번 넘겼으면 플래그를 해제
                                        } else if (dragAmount > 50 && currentPage > 0) {
                                            currentPage--
                                            isDragging = false // 한 번 넘겼으면 플래그를 해제
                                        }
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(
                            id = getImageResource(currentPage)
                        ),
                        contentDescription = "Guideline Image",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop, // 이미지 확대/축소 방식
                        modifier = Modifier
                            .fillMaxSize() // Box 크기 전체를 채움
                            .animateContentSize(animationSpec = tween(500)) // 0.5초에 한 번
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 인디케이터 (점 표시)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(totalPages) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == currentPage) 12.dp else 10.dp)   // 점이 너무 작으면 소멸함
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == currentPage) White else Grey
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // "시작하기" 버튼
                Button(
                    onClick = onComplete,
                    enabled = currentPage == totalPages - 1,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue,
                        disabledContainerColor = Black // 버튼 비활성화 시 배경과 일치하게
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "시작하기",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (currentPage == totalPages) White else Black
                    )
                }
            }
        }
    }
}

// 이미지 리소스 가져오기
fun getImageResource(page: Int): Int {
    return when (page) {
        0 -> R.drawable.guideline_1
        1 -> R.drawable.guideline_2
        2 -> R.drawable.guideline_3
        3 -> R.drawable.guideline_4
        4 -> R.drawable.guideline_5
        else -> R.drawable.guideline_6
    }
}
