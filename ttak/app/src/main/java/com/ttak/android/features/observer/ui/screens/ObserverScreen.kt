package com.ttak.android.features.observer.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttak.android.data.model.GoalState
import com.ttak.android.data.model.Time
import com.ttak.android.data.model.User
import com.ttak.android.data.repository.PreviewFriendStoryRepository
import com.ttak.android.data.repository.PreviewUserRepository
import com.ttak.android.features.observer.domain.repository.UserRepository
import com.ttak.android.features.observer.ui.components.*
import com.ttak.android.features.observer.viewmodel.FriendStoryViewModel
import com.ttak.android.features.observer.viewmodel.UserViewModel

@Composable
fun ObserverScreen(
    friendStoryViewModel: FriendStoryViewModel,
    userViewModel: UserViewModel,
    goalState: GoalState = GoalState()
) {
    val selectedFilterId by friendStoryViewModel.selectedFilterId.collectAsState()
    val filterOptions by friendStoryViewModel.filterOptions.collectAsState()
    val friends by friendStoryViewModel.friends.collectAsState()
    val searchResults by userViewModel.searchResults.collectAsState()

    var isListExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Content Layer (z-index: 1)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Carousel
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

                // 필터 (z-index: 2)
                FriendListFilter(
                    options = filterOptions,
                    selectedOptionId = selectedFilterId,
                    onOptionSelected = friendStoryViewModel::setSelectedFilter,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 친구 목록 영역 (z-index: 1)
                ExpandableFriendListContainer(
                    friends = friends,
                    onSearchUsers = { query ->
                        userViewModel.searchUsers(query)
                        searchResults
                    },
                    onUserSelect = { user ->
                        userViewModel.addFriend(user)
                    },
                    modifier = Modifier.fillMaxSize(),
                    onExpandedChanged = { expanded ->
                        isListExpanded = expanded
                    }
                )
            }
        }

        // PopupLayer (z-index: 100)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
        )
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

    val previewFriendStoryViewModel = FriendStoryViewModel(PreviewFriendStoryRepository())
    val previewUserViewModel = UserViewModel(PreviewUserRepository())
    previewFriendStoryViewModel.loadInitialData()

    ObserverScreen(
        friendStoryViewModel = previewFriendStoryViewModel,
        userViewModel = previewUserViewModel,
        goalState = previewGoalState
    )
}