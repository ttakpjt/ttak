package com.ttak.android.features.observer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ttak.android.data.model.FriendStory
import com.ttak.android.R

@Composable
fun FriendStoryItem(
    friend: FriendStory,
    modifier: Modifier = Modifier,
    onWaterBubbleClick: () -> Unit = {},
    onSpeechBubbleClick: () -> Unit = {}
) {
    var showPopup by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .width(80.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Original Content (프로필 이미지와 이름)
        Column(
            modifier = Modifier
                .clickable { showPopup = !showPopup },
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

        // Popup Menu - 별도의 Box로 분리
        if (showPopup) {
            androidx.compose.ui.window.Popup(
                onDismissRequest = { showPopup = false }
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .offset(y = (-15).dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(
                            color = Color(0xFF2C2C2C),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(
                            onClick = {
                                onWaterBubbleClick()
                                showPopup = false
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.water_bubble_icon),
                                contentDescription = "물방울",
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                onSpeechBubbleClick()
                                showPopup = false
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.speech_bubble_icon),
                                contentDescription = "말풍선",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}