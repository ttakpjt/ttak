import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ttak.android.common.navigation.AppScreens
import com.ttak.android.common.navigation.NavigationManager

@Composable
fun UnsetGoalCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val colors = listOf(
                Color(0xFF2E1065),  // 진한 보라
                Color(0xFF1E1B4B),  // 진한 남색
                Color(0xFF172554)   // 진한 파랑
            )
            val brush = Brush.verticalGradient(colors)

            drawRect(
                brush = brush,
                size = size
            )
        }

        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 150.dp, y = (-50).dp)
                .background(
                    Color(0x33A855F7),
                    CircleShape
                )
                .blur(radius = 70.dp)
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-50).dp, y = 150.dp)
                .background(
                    Color(0x333B82F6),
                    CircleShape
                )
                .blur(radius = 70.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .scale(scale)
                    .padding(horizontal = 16.dp, vertical = 32.dp),  // 수직 패딩 감소
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x33FFFFFF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✨ 당신의 목표를 \n설정할 시간이에요! ✨",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    NavigationManager.navigateTo(AppScreens.SetGoal)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7C3AED)
                ),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(42.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    "목표 설정하러 가기 🎯",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}