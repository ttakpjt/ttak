import android.app.Application
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttak.android.data.repository.history.HistoryRepositoryImpl
import com.ttak.android.features.observer.ui.components.PageIndicator
import com.ttak.android.features.observer.ui.components.SetGoalCard
import com.ttak.android.features.observer.viewmodel.GoalStateViewModel
import com.ttak.android.features.observer.viewmodel.GoalStateViewModelFactory
import com.ttak.android.features.observer.viewmodel.ObserverViewModel
import com.ttak.android.features.observer.viewmodel.ObserverViewModelFactory
import com.ttak.android.network.util.ApiConfig

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardCarousel(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    // API 인스턴스 생성
    val historyApi = ApiConfig.createHistoryApi(context)
    val historyRepository = HistoryRepositoryImpl(historyApi)

    // ViewModel 생성 - historyRepository 주입
    val viewModel: GoalStateViewModel = viewModel(
        factory = GoalStateViewModelFactory(application, historyRepository)
    )

    // 주간 딱걸림 횟수 가져오기
    val observerViewModel: ObserverViewModel = viewModel(
        factory = ObserverViewModelFactory(application)
    )
    observerViewModel.getPickRank()

    val goalState by viewModel.goalState.collectAsState()
    val countData by observerViewModel.countData.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 2 })

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
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

                // 페이지 내용
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
                            countData?.let { data ->
                                Dashboard(countData = data)
                            }
                        }
                    }
                }

                PageIndicator(
                    pageCount = 2,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}