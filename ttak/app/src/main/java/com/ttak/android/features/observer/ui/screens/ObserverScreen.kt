package com.ttak.android.features.observer.ui.screens

import CardCarousel
import MessageDialog
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttak.android.domain.model.FriendStory
import com.ttak.android.domain.model.GoalState
import com.ttak.android.data.repository.PreviewFriendStoryRepository
import com.ttak.android.data.repository.PreviewUserRepository
import com.ttak.android.data.repository.UserRepositoryImpl
import com.ttak.android.features.observer.ui.components.*
import com.ttak.android.features.observer.viewmodel.FriendStoryViewModel
import com.ttak.android.features.observer.viewmodel.FriendStoryViewModelFactory
import com.ttak.android.features.observer.viewmodel.UserViewModel
import com.ttak.android.features.observer.viewmodel.UserViewModelFactory
import com.ttak.android.network.api.PreviewUserApi

@Composable
fun ObserverScreen() {
    // 목업 API와 Repository 사용
    val userRepository = UserRepositoryImpl(PreviewUserApi())
    val friendStoryRepository = PreviewFriendStoryRepository()

    // ViewModel Factory 생성
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

//@Composable
//private fun ObserverScreenContent(
//    friendStoryViewModel: FriendStoryViewModel,
//    userViewModel: UserViewModel,
//    goalState: GoalState = GoalState()
//) {
//    val selectedFilterId by friendStoryViewModel.selectedFilterId.collectAsState()
//    val filterOptions by friendStoryViewModel.filterOptions.collectAsState()
//    val friends by friendStoryViewModel.friends.collectAsState()
//    val searchResults by userViewModel.searchResults.collectAsState()
//
//    var isListExpanded by remember { mutableStateOf(false) }
//    var showPopup by remember { mutableStateOf(false) }
//    var selectedFriend by remember { mutableStateOf<FriendStory?>(null) }
//    var popupOffset by remember { mutableStateOf(Offset.Zero) }
//    var showMessageDialog by remember { mutableStateOf(false) }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .clickable(
//                    enabled = showPopup,
//                    indication = null,
//                    interactionSource = remember { MutableInteractionSource() }
//                ) {
//                    if (showPopup) {
//                        showPopup = false
//                    }
//                }
//        ) {
//            // Carousel
//            AnimatedVisibility(
//                visible = !isListExpanded,
//                enter = fadeIn() + slideInVertically(),
//                exit = fadeOut() + slideOutVertically()
//            ) {
//                CardCarousel(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 16.dp)
//                )
//            }
//
//            // Friend List with integrated Filter
//            Box(modifier = Modifier.fillMaxSize()) {
//                ExpandableFriendListContainer(
//                    friends = friends,
//                    filterOptions = filterOptions,
//                    selectedFilterId = selectedFilterId,
//                    onFilterSelected = friendStoryViewModel::setSelectedFilter,
//                    onSearchUsers = { query ->
//                        userViewModel.searchUsers(query)
//                        searchResults
//                    },
//                    onUserSelect = { user ->
//                        userViewModel.addFriend(user)
//                    },
//                    modifier = Modifier.fillMaxSize(),
//                    onExpandedChanged = { expanded ->
//                        isListExpanded = expanded
//                    },
//                    onShowPopup = { friend, offset ->
//                        selectedFriend = friend
//                        popupOffset = offset
//                        showPopup = true
//                        Log.d("ObserverScreen", "Clicked Friend: ${friend}")
//                    }
//                )
//
//                // Popup without modifier parameter
//                if (showPopup && selectedFriend != null) {
//                    Box(modifier = Modifier.fillMaxSize()) {
//                        PopupMenu(
//                            onDismiss = {
//                                Log.d("ObserverScreen", "PopupMenu dismissed")
//                                showPopup = false
//                            },
//                            offset = popupOffset,
//                            onWaterBubbleClick = {
//                                Log.d("ObserverScreen", "Water bubble clicked")
//                                showPopup = false
//                            },
//                            onSpeechBubbleClick = {
//                                Log.d("ObserverScreen", "Speech bubble clicked")
//                                showPopup = false
//                                showMessageDialog = true
//                            }
//                        )
//                    }
//                }
//
//                // Message Dialog
//                if (showMessageDialog && selectedFriend != null) {
//                    Log.d("ObserverScreen", "Showing MessageDialog for friend: ${selectedFriend?.name}")
//                    MessageDialog(
//                        friendStory = selectedFriend!!,
//                        onDismiss = {
//                            Log.d("ObserverScreen", "MessageDialog dismissed")
//                            showMessageDialog = false
//                        },
//                        onSend = { message ->
//                            Log.d("ObserverScreen", "Sending message to ${selectedFriend!!.name}: $message")
//                            // TODO: 메시지 전송 로직 구현
//                            showMessageDialog = false
//                        }
//                    )
//                }
//            }
//        }
//    }
//}

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

    var isListExpanded by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }
    var selectedFriend by remember { mutableStateOf<FriendStory?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        .padding(vertical = 16.dp)
                )
            }

            // Friend List with integrated Filter
            Box(modifier = Modifier.fillMaxSize()) {
                ExpandableFriendListContainer(
                    friends = friends,
                    filterOptions = filterOptions,
                    selectedFilterId = selectedFilterId,
                    onFilterSelected = friendStoryViewModel::setSelectedFilter,
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

                // Message Dialog
                if (showMessageDialog && selectedFriend != null) {
                    MessageDialog(
                        friendStory = selectedFriend!!,
                        onDismiss = {
                            showMessageDialog = false
                        },
                        onSend = { message ->
                            Log.d("ObserverScreen", "Sending message to ${selectedFriend!!.name}: $message")
                            // TODO: 메시지 전송 로직 구현
                            showMessageDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ObserverScreenPreview() {
    val previewFriendStoryViewModel = FriendStoryViewModel(PreviewFriendStoryRepository())
    val previewUserViewModel = UserViewModel(PreviewUserRepository())
    previewFriendStoryViewModel.loadInitialData()

    ObserverScreenContent(
        friendStoryViewModel = previewFriendStoryViewModel,
        userViewModel = previewUserViewModel
    )
}