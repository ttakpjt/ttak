package com.ttak.android.features.observer.ui.screens

import CardCarousel
import MessageDialog
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttak.android.domain.model.FriendStory
import com.ttak.android.domain.model.GoalState
import com.ttak.android.data.repository.PreviewFriendStoryRepository
import com.ttak.android.data.repository.UserRepositoryImpl
import com.ttak.android.features.observer.ui.components.*
import com.ttak.android.features.observer.viewmodel.FriendStoryViewModel
import com.ttak.android.features.observer.viewmodel.FriendStoryViewModelFactory
import com.ttak.android.features.observer.viewmodel.UserViewModel
import com.ttak.android.features.observer.viewmodel.UserViewModelFactory
import com.ttak.android.network.util.ApiConfig

@Composable
fun ObserverScreen() {
    val context = LocalContext.current  // Context 가져오기

    // API 및 Repository 초기화
    val userApi = ApiConfig.createUserApi(context)
    val userRepository = UserRepositoryImpl(userApi)
    val friendStoryRepository = PreviewFriendStoryRepository()

    // ViewModel 초기화
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )
    val friendStoryViewModel: FriendStoryViewModel = viewModel(
        factory = FriendStoryViewModelFactory(friendStoryRepository)
    )

    ObserverScreenContent(
        friendStoryViewModel = friendStoryViewModel,
        userViewModel = userViewModel
    )
}

@Composable
private fun ObserverScreenContent(
    friendStoryViewModel: FriendStoryViewModel,
    userViewModel: UserViewModel,
    goalState: GoalState = GoalState()
) {
    val selectedFilterId by friendStoryViewModel.selectedFilterId.collectAsState()
    val filterOptions by friendStoryViewModel.filterOptions.collectAsState()
    val friends by friendStoryViewModel.friends.collectAsState()
    val searchResults by userViewModel.searchResults.collectAsState()
    val uiState by userViewModel.uiState.collectAsState()

    var isListExpanded by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }
    var selectedFriend by remember { mutableStateOf<FriendStory?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = !isListExpanded,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                CardCarousel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                ExpandableFriendListContainer(
                    friends = friends,
                    filterOptions = filterOptions,
                    selectedFilterId = selectedFilterId,
                    onFilterSelected = friendStoryViewModel::setSelectedFilter,
                    onSearchUsers = { query ->
                        userViewModel.searchUsers(query)
                    },
                    searchResults = searchResults,  // Flow로 받은 검색 결과 전달
                    onUserSelect = { user ->
                        // userViewModel.addFriend(user)
                    },
                    modifier = Modifier.fillMaxSize(),
                    onExpandedChanged = { expanded ->
                        isListExpanded = expanded
                    },
                    onWaterBubbleClick = { friend ->
                        Log.d("ObserverScreen", "Water bubble clicked for: ${friend.name}")
                    },
                    onSpeechBubbleClick = { friend ->
                        Log.d("ObserverScreen", "Speech bubble clicked for: ${friend.name}")
                        selectedFriend = friend
                        showMessageDialog = true
                    }
                )

                if (showMessageDialog && selectedFriend != null) {
                    MessageDialog(
                        friendStory = selectedFriend!!,
                        onDismiss = {
                            showMessageDialog = false
                        },
                        onSend = { message ->
//                            Log.d("ObserverScreen", "Sending message to ${selectedFriend!!.userName}: $message")  // name -> userName
                            showMessageDialog = false
                        }
                    )
                }
            }
        }
    }
}