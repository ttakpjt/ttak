package com.ttak.android.features.observer.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ttak.android.data.model.GoalState
import com.ttak.android.data.model.Time
import com.ttak.android.data.repository.PreviewFriendStoryRepository
import com.ttak.android.features.observer.ui.components.*
import com.ttak.android.features.observer.viewmodel.FriendStoryViewModel

@Composable
fun ObserverScreen(
    viewModel: FriendStoryViewModel,
    goalState: GoalState = GoalState()
) {
    val selectedFilterId by viewModel.selectedFilterId.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    val friends by viewModel.friends.collectAsState()

    var isListExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Carousel (리스트가 확장되면 숨김)
            AnimatedVisibility(
                visible = !isListExpanded,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                CardCarousel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    goalState = goalState
                )
            }

            // 필터
            FriendListFilter(
                options = filterOptions,
                selectedOptionId = selectedFilterId,
                onOptionSelected = viewModel::setSelectedFilter,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 확장 가능한 친구 목록
            ExpandableFriendListContainer(
                friends = friends,
                onAddFriendClick = { /* 친구 추가 처리 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onExpandedChanged = { expanded ->
                    isListExpanded = expanded
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ObserverScreenPreview() {
    val previewGoalState = GoalState(
        isSet = true,
        startTime = Time(9, 0),
        endTime = Time(18, 0),
        currentTime = Time(13, 30)
    )

    val previewViewModel = FriendStoryViewModel(PreviewFriendStoryRepository())
    previewViewModel.loadInitialData()

    ObserverScreen(
        viewModel = previewViewModel,
        goalState = previewGoalState
    )
}