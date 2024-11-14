package com.ttak.android.features.observer.ui.screens

import CardCarousel
import MessageDialog
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttak.android.domain.model.FriendStory
import com.ttak.android.domain.model.GoalState
import com.ttak.android.domain.model.MessageData
import com.ttak.android.features.observer.ui.components.ExpandableFriendListContainer
import com.ttak.android.features.observer.viewmodel.FriendStoryViewModel
import com.ttak.android.features.observer.viewmodel.UserViewModel
import com.ttak.android.features.observer.viewmodel.UserViewModelFactory
import com.ttak.android.network.implementation.UserApiImpl
import com.ttak.android.network.util.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ObserverScreen(
    friendStoryViewModel: FriendStoryViewModel
) {
    val context = LocalContext.current

    // API 및 Repository 초기화
    val userApi = ApiConfig.createUserApi(context)
    val userRepository = UserApiImpl(userApi)

    // UserViewModel만 초기화
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )

    LaunchedEffect(Unit) {
        // 화면에 진입할 때마다 데이터 새로고침
        friendStoryViewModel.refreshFriends()
    }

    ObserverScreenContent(
        friendStoryViewModel = friendStoryViewModel,
        userViewModel = userViewModel,
        goalState = GoalState()
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



    LaunchedEffect(uiState) {
        when (uiState) {
            is UserViewModel.UiState.Success -> {
                // 친구 추가 성공 시 친구 목록 새로고침
                friendStoryViewModel.refreshFriends()
            }
            is UserViewModel.UiState.Error -> {
                Log.e("ObserverScreen", "Friend add error: ${(uiState as UserViewModel.UiState.Error).message}")
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(20.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

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
                        userViewModel.addFriend(user)
                    },
                    modifier = Modifier.fillMaxSize(),
                    onExpandedChanged = { expanded ->
                        isListExpanded = expanded
                    },
                    onWaterBubbleClick = { friend ->
                        Log.d("ObserverScreen", "Water bubble clicked for: ${friend.friendName}")  // name -> friendName
                        Log.d("ObserverScreen", "Water bubble clicked for: ${friend.friendName}")
                        CoroutineScope(Dispatchers.IO).launch {
                            val messageData = MessageData(
                                userId = selectedFriend!!.friendId,
                                data = "WaterBalloon"
                            )
                            try {
                                val response = messageApi.sendItem(messageData)
                                if (response.isSuccessful) {
                                    Log.d("ObserverScreen", "Item sent successfully")
                                } else {
                                    Log.e("ObserverScreen", "Failed to send Item: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                Log.e("ObserverScreen", "Error sending Item", e)
                            }
                        }
                    },
                    onSpeechBubbleClick = { friend ->
                        Log.d("ObserverScreen", "Speech bubble clicked for: ${friend.friendName}")  // name -> friendName
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
                            showMessageDialog = false
                            CoroutineScope(Dispatchers.IO).launch {
                                val messageData = MessageData(
                                    userId = selectedFriend!!.friendId,
                                    data = message
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