import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.ttak.android.R
import com.ttak.android.common.navigation.AppScreens
import com.ttak.android.common.navigation.NavigationManager
import com.ttak.android.features.observer.ui.components.SetGoalCard
import com.ttak.android.features.observer.viewmodel.GoalStateViewModel
import com.ttak.android.features.observer.viewmodel.GoalStateViewModelFactory

@Composable
fun SmallGoalCard(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: GoalStateViewModel = viewModel(
        factory = GoalStateViewModelFactory(application)
    )
    val goalState by viewModel.goalState.collectAsState()

    Card(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .height(80.dp)
            .then(
                if (!goalState.isSet) {
                    Modifier.clickable {
                        NavigationManager.navigateTo(AppScreens.SetGoal)
                    }
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2F2F32)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        if (!goalState.isSet) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.prohibition_icon),
                        contentDescription = "Friends Icon",
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "목표를 설정하고 집중해보세요!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.right_icon),
                    contentDescription = "Next",
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.prohibition_icon),
                        contentDescription = "Friends Icon",
                        modifier = Modifier.size(24.dp)
                    )

                    // 시간 텍스트
                    Text(
                        text = "${String.format("%02d:%02d", goalState.startTime.hour, goalState.startTime.minute)} - ${String.format("%02d:%02d", goalState.endTime.hour, goalState.endTime.minute)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "(",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    // 앱 아이콘들
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        goalState.selectedApps.take(3).forEach { app ->
                            Image(
                                painter = rememberDrawablePainter(drawable = app.icon),
                                contentDescription = "App Icon - ${app.appName}",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                        if (goalState.selectedApps.size > 3) {
                            Text(
                                text = "...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = ")",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}