import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ttak.android.domain.model.FriendStory
import com.ttak.android.features.observer.ui.components.PopupMenu
import kotlin.io.path.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FriendStoryItem(
    friend: FriendStory,
    modifier: Modifier = Modifier,
    onWaterBubbleClick: (FriendStory) -> Unit = {},
    onSpeechBubbleClick: (FriendStory) -> Unit = {}
) {
    var showPopup by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    val satelliteOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    fun createStarPath(center: Offset, outerRadius: Float, innerRadius: Float, points: Int = 5): Path {
        return Path().apply {
            val angleStep = (2f * PI / points).toFloat()
            moveTo(
                x = center.x + outerRadius * cos(0f),
                y = center.y + outerRadius * sin(0f)
            )

            for (i in 1 until points * 2) {
                val radius = if (i % 2 == 0) outerRadius else innerRadius
                val angle = i * angleStep / 2
                lineTo(
                    x = center.x + radius * cos(angle),
                    y = center.y + radius * sin(angle)
                )
            }
            close()
        }
    }

    Box(
        modifier = modifier
            .width(120.dp)
            .height(100.dp)
            .clickable(enabled = friend.hasNewStory) { showPopup = !showPopup },
        contentAlignment = Alignment.Center
    ) {
        // 배경 별들
        Canvas(modifier = Modifier.fillMaxSize()) {
            repeat(12) { index ->
                val x = (index * 25 + orbitAngle/2) % size.width
                val y = (index * 30 + orbitAngle/3) % size.height
                val starPath = createStarPath(
                    center = Offset(x, y),
                    outerRadius = (4 + index % 3).dp.toPx(),
                    innerRadius = (2 + index % 3).dp.toPx()
                )
                drawPath(
                    path = starPath,
                    color = Color.White.copy(alpha = 0.4f)
                )
            }
        }

        // 궤도 그리기 (이전과 동일)
        Box(
            modifier = Modifier
                .size(90.dp)
                .rotate(orbitAngle)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0x33FFFFFF),
                    style = Stroke(width = 1.dp.toPx()),
                    radius = size.minDimension / 2
                )

                // 위성 (별 모양으로 변경)
                if (friend.hasNewStory) {
                    val radius = size.width / 2
                    val x = radius + radius * cos(satelliteOffset) * 0.8f
                    val y = radius + radius * sin(satelliteOffset) * 0.8f

                    val satelliteStarPath = createStarPath(
                        center = Offset(x, y),
                        outerRadius = 6.dp.toPx(),
                        innerRadius = 3.dp.toPx()
                    )
                    drawPath(
                        path = satelliteStarPath,
                        color = Color(0xFFFF5E5E)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(70.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = if (!friend.hasNewStory)
                        Color(0xFF8C60D9).copy(alpha = 0.9f)
                    else
                        Color(0xFFFF5E5E).copy(alpha = 0.7f),
                    style = Stroke(width = 6.dp.toPx()),
                    radius = size.minDimension / 2
                )
            }
        }

        // 중앙 행성 (프로필 이미지)
        Box(
            modifier = Modifier
                .size(60.dp)
        ) {
            // 행성 링
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (friend.hasNewStory) {
                    rotate(45f) {
                        drawOval(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFF5E5E),
                                    Color(0xFFFF5E5E)
                                )
                            ),
                            size = Size(size.width, size.height * 0.2f),
                            topLeft = Offset(0f, size.height * 0.4f)
                        )
                    }
                }
            }

            // 프로필 이미지
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(friend.friendImg)
                    .crossfade(true)
                    .build(),
                contentDescription = "${friend.friendName}의 프로필 이미지",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                error = painterResource(id = android.R.drawable.ic_menu_report_image)
            )
        }

        // 이름
        Text(
            text = friend.friendName,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )

        if (showPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showPopup = false }
            )

            Box(
                modifier = Modifier
                    .offset(y = (-70).dp)
                    .zIndex(1f)
                    .clickable { }
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