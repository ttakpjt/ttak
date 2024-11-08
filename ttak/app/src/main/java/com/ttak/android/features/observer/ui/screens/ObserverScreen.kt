package com.ttak.android.features.observer.ui.screens

import CardCarousel
import MessageDialog
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttak.android.domain.model.FriendStory
import com.ttak.android.domain.model.GoalState
import com.ttak.android.data.repository.PreviewFriendStoryRepository
import com.ttak.android.data.repository.PreviewUserRepository
import com.ttak.android.data.repository.UserRepositoryImpl
import com.ttak.android.domain.model.MessageData
import com.ttak.android.features.observer.ui.components.*
import com.ttak.android.features.observer.viewmodel.FriendStoryViewModel
import com.ttak.android.features.observer.viewmodel.FriendStoryViewModelFactory
import com.ttak.android.features.observer.viewmodel.UserViewModel
import com.ttak.android.features.observer.viewmodel.UserViewModelFactory
import com.ttak.android.network.implementation.UserApiImpl
import com.ttak.android.network.util.ApiConfig
import com.ttak.android.network.api.PreviewUserApi
import com.ttak.android.network.util.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ObserverScreen() {
    val context = LocalContext.current  // Context 가져오기

    // API 및 Repository 초기화
    val userApi = ApiConfig.createUserApi(context)
    val userRepository = UserApiImpl(userApi)  // UserRepositoryImpl -> UserApiImpl
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
    val context = LocalContext.current
    val messageApi = ApiConfig.createMessageApi(context)

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
                    searchResults = searchResults,
                    onUserSelect = { user ->
                        Log.d("ObserverScreen", "Adding friend: ${user.userName} (ID: ${user.userId})")
                        userViewModel.addFriend(user)  // 주석 해제
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

                LaunchedEffect(uiState) {
                    when (uiState) {
                        is UserViewModel.UiState.Success -> {
                            // 친구 추가 성공 처리 (필요한 경우)
                        }
                        is UserViewModel.UiState.Error -> {
                            // 에러 처리 (필요한 경우 Toast나 Snackbar 표시)
                            Log.e("ObserverScreen", "Friend add error: ${(uiState as UserViewModel.UiState.Error).message}")
                        }
                        else -> {}
                    }
                }


                if (showMessageDialog && selectedFriend != null) {
                    MessageDialog(
                        friendStory = selectedFriend!!,
                        onDismiss = {
                            showMessageDialog = false
                        },
                        onSend = { message ->
//                            Log.d("ObserverScreen", "Sending message to ${selectedFriend!!.userName}: $message")  // name -> userName
                            showMessageDialog = false
                            CoroutineScope(Dispatchers.IO).launch {
                                val messageData = MessageData(
                                    userId = selectedFriend!!.id,
                                    message = message
                                )
                                try {
                                    val response = messageApi.sendMessage(messageData)
                                    if (response.isSuccessful) {
                                        Log.d("ObserverScreen", "Message sent successfully")
                                    } else {
                                        Log.e("ObserverScreen", "Failed to send message: ${response.code()}")
                                    }
                                } catch (e: Exception) {
                                    Log.e("ObserverScreen", "Error sending message", e)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}