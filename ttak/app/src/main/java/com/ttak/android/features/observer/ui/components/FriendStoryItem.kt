//package com.ttak.android.features.observer.ui.components
//
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.layout.onGloballyPositioned
//import androidx.compose.ui.layout.positionInWindow
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.unit.toSize
//import coil.compose.AsyncImage
//import coil.request.ImageRequest
//import com.ttak.android.domain.model.FriendStory
//
//@Composable
//fun FriendStoryItem(
//    friend: FriendStory,
//    modifier: Modifier = Modifier,
//    onShowPopup: (FriendStory, Offset) -> Unit = { _, _ -> },
//) {
//    var position by remember { mutableStateOf(Offset.Zero) }
//    var size by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
//
//    Box(
//        modifier = modifier
//            .width(80.dp)
//            .wrapContentHeight()
//            .onGloballyPositioned { coordinates ->
//                position = coordinates.positionInWindow()
//                size = coordinates.size.toSize()
//            },
//        contentAlignment = Alignment.TopCenter
//    ) {
//        Column(
//            modifier = Modifier.then(
//                if (friend.hasNewStory) {
//                    Modifier.clickable {
//                        onShowPopup(
//                            friend,
//                            Offset(
//                                x = position.x + 15f,
//                                y = position.y - (size.height * 1.5f) - 550f
//                            )
//                        )
//                    }
//                } else {
//                    Modifier
//                }
//            ),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(80.dp)
//                    .border(
//                        width = 2.dp,
//                        color = if (friend.hasNewStory) Color(0xFFFF5E5E) else Color(0xFF7FEC93),
//                        shape = CircleShape
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                AsyncImage(
//                    model = ImageRequest.Builder(LocalContext.current)
//                        .data(friend.profileImageUrl)
//                        .crossfade(true)
//                        .build(),
//                    contentDescription = "${friend.name}의 프로필 이미지",
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(8.dp)
//                        .clip(CircleShape),
//                    contentScale = ContentScale.Crop,
//                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
//                    error = painterResource(id = android.R.drawable.ic_menu_report_image)
//                )
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = friend.name,
//                color = Color.White,
//                fontSize = 16.sp,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 4.dp)
//            )
//        }
//    }
//}

package com.ttak.android.features.observer.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ttak.android.domain.model.FriendStory

@Composable
fun FriendStoryItem(
    friend: FriendStory,
    modifier: Modifier = Modifier,
    onWaterBubbleClick: (FriendStory) -> Unit = {},
    onSpeechBubbleClick: (FriendStory) -> Unit = {}
) {
    var showPopup by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.width(80.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // 메인 컨텐츠
        Column(
            modifier = Modifier.then(
                if (friend.hasNewStory) {
                    Modifier.clickable { showPopup = !showPopup }
                } else {
                    Modifier
                }
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(
                        width = 2.dp,
                        color = if (friend.hasNewStory) Color(0xFFFF5E5E) else Color(0xFF7FEC93),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(friend.profileImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${friend.name}의 프로필 이미지",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                    error = painterResource(id = android.R.drawable.ic_menu_report_image)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = friend.name,
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
        }

        // 팝업 메뉴 (오버레이)와 배경 클릭 영역
        if (showPopup) {
            // 전체 화면을 덮는 투명한 클릭 영역
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showPopup = false }
            )

            // 팝업 메뉴
            Box(
                modifier = Modifier
                    .offset(y = (-30).dp)
                    .clickable { /* 팝업 영역 클릭 시 이벤트 전파 방지 */ }
            ) {
                PopupMenu(
                    onDismiss = { showPopup = false },
                    onWaterBubbleClick = { onWaterBubbleClick(friend) },
                    onSpeechBubbleClick = { onSpeechBubbleClick(friend) }
                )
            }
        }
    }
}