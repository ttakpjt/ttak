import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ttak.android.domain.model.CountResponse
import kotlin.math.sin

@Composable
fun Dashboard(
    countData: CountResponse,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "검색 아이콘",
                tint = Color(0xFFE879F9),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "주간 딱걸림 리포트",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 통계 카드들
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "전체",
                count = countData.data.totalCount,
                color = Color(0xFF818CF8)
            )

            StatCard(
                modifier = Modifier.weight(1f),
                title = "친구들",
                count = countData.data.friendsCount,
                color = Color(0xFFA78BFA)
            )
        }

        Spacer(modifier = Modifier.height(12.dp)) // 여백 줄임

        // 나의 통계 카드
        MyStatsCard(
            myCount = countData.data.myCount,
            totalCount = countData.data.totalCount
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    color: Color
) {
    Surface(
        modifier = modifier,
        color = Color(0x33FFFFFF),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp), // 여백 줄임
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp)) // 여백 줄임

            Text(
                text = buildAnnotatedString {
                    append(count.toString())
                    withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.7f))) {
                        append(" 회")
                    }
                },
                style = MaterialTheme.typography.bodyLarge.copy(color = color), // 글씨 크기 줄임
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MyStatsCard(
    myCount: Int,
    totalCount: Int
) {
    val percentage = if (totalCount > 0) {
        (myCount.toFloat() / totalCount.toFloat() * 100).toInt()
    } else {
        0
    }

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        color = Color(0x33FFFFFF),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = Color(0xFFE879F9),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "나의 딱걸림",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = buildAnnotatedString {
                            append(myCount.toString())
                            withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.7f))) {
                                append(" 회")
                            }
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(Color(0xFFE879F9)),
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0x11FFFFFF), CircleShape)
                        .border(1.dp, Color(0xFFE879F9).copy(alpha = 0.3f), CircleShape)
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val radiusPx = size.minDimension / 2
                        val centerX = size.width / 2
                        val centerY = size.height / 2

                        drawCircle(
                            color = Color(0xFFE879F9).copy(alpha = 0.1f),
                            radius = radiusPx,
                            center = Offset(centerX, centerY)
                        )

                        clipPath(androidx.compose.ui.graphics.Path().apply {
                            addOval(Rect(0f, 0f, size.width, size.height))
                        }) {
                            val fillHeight = size.height * (1 - percentage / 100f)
                            drawRect(
                                color = Color(0xFFE879F9).copy(alpha = 0.5f),
                                topLeft = Offset(0f, fillHeight),
                                size = Size(size.width, size.height - fillHeight)
                            )

                            val wavePath = androidx.compose.ui.graphics.Path().apply {
                                val waveHeight = radiusPx * 0.15f  // 물결 높이를 좀 줄임
                                val periods = 3f

                                moveTo(0f, fillHeight)
                                for (x in 0..size.width.toInt()) {
                                    val phase = x / size.width * 2 * Math.PI * periods
                                    val y = fillHeight + sin(phase + waveOffset * 2 * Math.PI) * waveHeight
                                    lineTo(x.toFloat(), y.toFloat())
                                }
                                lineTo(size.width, size.height)
                                lineTo(0f, size.height)
                                close()
                            }

                            drawPath(
                                path = wavePath,
                                color = Color(0xFFE879F9).copy(alpha = 0.7f),
                                style = Fill
                            )
                        }
                    }

                    Text(
                        text = "${percentage}%",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}