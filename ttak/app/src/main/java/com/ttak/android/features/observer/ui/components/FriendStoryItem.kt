package com.ttak.android.features.observer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ttak.android.data.model.FriendStory

@Composable
fun FriendStoryItem(
    friend: FriendStory,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(80.dp),  // 전체 너비를 이미지 크기와 동일하게 설정
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .border(
                    width = 2.dp,
                    color = if (friend.hasNewStory) Color(0xFF4CAF50) else Color.Gray,
                    shape = CircleShape
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(friend.profileImageUrl)
                    .crossfade(true)
                    .build()
            )

            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Gray
                    )
                }
                is AsyncImagePainter.State.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = CircleShape
                            )
                    )
                }
                else -> {
                    Image(
                        painter = painter,
                        contentDescription = friend.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))  // 간격을 조금 더 늘림
        Text(
            text = friend.name,
            color = Color.White,
            fontSize = 16.sp,  // 적절한 텍스트 크기 설정
            textAlign = TextAlign.Center,  // 텍스트 중앙 정렬
            modifier = Modifier
                .fillMaxWidth()  // 전체 너비를 사용
                .padding(horizontal = 4.dp)
        )
    }
}