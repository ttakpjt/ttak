package com.ttak.android.features.observer.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ttak.android.data.model.GoalState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardCarousel(
    modifier: Modifier = Modifier,
    goalState: GoalState = GoalState(),
    onSetGoal: () -> Unit = {}
) {
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
                            UnsetGoalCard(onSetGoal)
                        } else {
                            SetGoalCard(goalState)
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