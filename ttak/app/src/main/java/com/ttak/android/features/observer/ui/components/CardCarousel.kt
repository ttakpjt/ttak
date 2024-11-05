import android.app.Application
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttak.android.data.local.AppDatabase
import com.ttak.android.data.repository.FocusGoalRepository
import com.ttak.android.features.observer.ui.components.PageIndicator
import com.ttak.android.features.observer.ui.components.SetGoalCard
import com.ttak.android.features.observer.ui.components.UnsetGoalCard
import com.ttak.android.features.observer.viewmodel.GoalStateViewModel
import com.ttak.android.features.observer.viewmodel.GoalStateViewModelFactory

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardCarousel(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Application context 가져오기
    val application = context.applicationContext as Application

    // ViewModel 생성
    val viewModel: GoalStateViewModel = viewModel(
        factory = GoalStateViewModelFactory(application)
    )

    val goalState by viewModel.goalState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 2 })

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(250.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2F2F32)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (page) {
                    0 -> {
                        if (!goalState.isSet) {
                            UnsetGoalCard()
                        } else {
                            SetGoalCard(goalState = goalState)
                        }
                    }
                    1 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            Text(
                                "두 번째 카드",
                                color = Color.White
                            )
                        }
                    }
                }

                PageIndicator(
                    pageCount = 2,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}