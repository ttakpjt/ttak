package com.ttak.android.features.observer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ttak.android.features.observer.ui.components.*
import com.ttak.android.data.model.GoalState
import com.ttak.android.data.model.Time
import com.ttak.android.data.repository.PreviewFriendStoryRepository
import com.ttak.android.features.observer.viewmodel.FriendStoryViewModel

@Composable
fun ObserverScreen(
    viewModel: FriendStoryViewModel,
    goalState: GoalState = GoalState()
) {
    val selectedFilterId by viewModel.selectedFilterId.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    val friends by viewModel.friends.collectAsState()

    Column {
        CardCarousel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            goalState = goalState
        )

        FriendListFilter(
            options = filterOptions,
            selectedOptionId = selectedFilterId,
            onOptionSelected = viewModel::setSelectedFilter
        )

        Spacer(modifier = Modifier.height(16.dp))

        FriendList(
            friends = friends,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ObserverScreenPreview() {
    // 미리보기용 GoalState 생성
    val previewGoalState = GoalState(
        isSet = true,
        startTime = Time(9, 0),
        endTime = Time(18, 0),
        currentTime = Time(13, 30)
    )

    // 미리보기용 ViewModel 생성
    val previewViewModel = FriendStoryViewModel(PreviewFriendStoryRepository())

    // 초기 데이터 로드
    previewViewModel.loadInitialData()

    // ObserverScreen 렌더링
    ObserverScreen(
        viewModel = previewViewModel,
        goalState = previewGoalState
    )
}
