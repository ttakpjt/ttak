package com.ttak.android.features.observer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ttak.android.R
import kotlin.math.roundToInt

@Composable
fun PopupMenu(
    onDismiss: () -> Unit,
    offset: Offset,
    onWaterBubbleClick: () -> Unit,
    onSpeechBubbleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .width(100.dp)
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
                    onDismiss()
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
                    onDismiss()
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