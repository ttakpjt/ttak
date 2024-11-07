package com.ttak.android.features.observer.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.ttak.android.domain.model.FilterOption
import com.ttak.android.domain.model.FriendStory
import com.ttak.android.domain.model.User
import kotlinx.coroutines.async
import kotlin.math.abs

//@Composable
//fun ExpandableFriendListContainer(
//    friends: List<FriendStory>,
//    filterOptions: List<FilterOption>,
//    selectedFilterId: Int,
//    onFilterSelected: (Int) -> Unit,
//    onSearchUsers: suspend (String) -> List<User>,
//    onUserSelect: (User) -> Unit,
//    modifier: Modifier = Modifier,
//    onExpandedChanged: (Boolean) -> Unit,
//    onShowPopup: (FriendStory, Offset) -> Unit
//) {
//    var isExpanded by remember { mutableStateOf(false) }
//    var dragOffset by remember { mutableStateOf(0f) }
//    var isDragging by remember { mutableStateOf(false) }
//    val scope = rememberCoroutineScope()
//
//    LaunchedEffect(isExpanded) {
//        onExpandedChanged(isExpanded)
//    }
//
//    Box(modifier = modifier) {
//        Column {
//            // Friend List Filter
//            FriendListFilter(
//                options = filterOptions,
//                selectedOptionId = selectedFilterId,
//                onOptionSelected = onFilterSelected,
//                modifier = Modifier.padding(bottom = 16.dp)
//            )
//
//            // Drag handler area
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(8.dp)
//                    .pointerInput(Unit) {
//                        detectDragGestures(
//                            onDragStart = { isDragging = true },
//                            onDragEnd = {
//                                isDragging = false
//                                if (abs(dragOffset) > 100f) {
//                                    isExpanded = dragOffset < 0
//                                }
//                                dragOffset = 0f
//                            },
//                            onDragCancel = {
//                                isDragging = false
//                                dragOffset = 0f
//                            },
//                            onDrag = { change: PointerInputChange, dragAmount: Offset ->
//                                change.consume()
//                                dragOffset += dragAmount.y
//                            }
//                        )
//                    }
//            )
//
//            // Friends list
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(3),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .animateContentSize(
//                        animationSpec = spring(
//                            dampingRatio = Spring.DampingRatioMediumBouncy,
//                            stiffness = Spring.StiffnessLow
//                        )
//                    ),
//                contentPadding = PaddingValues(horizontal = 16.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                item {
//                    AddFriendItem(
//                        searchUsers = { query ->
//                            scope.async {
//                                onSearchUsers(query)
//                            }.await()
//                        },
//                        onUserSelect = onUserSelect
//                    )
//                }
//
//                items(if (isExpanded) friends else friends.take(8)) { friend ->
//                    FriendStoryItem(
//                        friend = friend,
//                        onShowPopup = onShowPopup
//                    )
//                }
//            }
//
//            // More/Collapse button
//            TextButton(
//                onClick = { isExpanded = !isExpanded },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp)
//            ) {
//                Text(
//                    text = if (isExpanded) "접기" else "더보기",
//                    style = MaterialTheme.typography.labelLarge
//                )
//            }
//        }
//    }
//}

@Composable
fun ExpandableFriendListContainer(
    friends: List<FriendStory>,
    filterOptions: List<FilterOption>,
    selectedFilterId: Int,
    onFilterSelected: (Int) -> Unit,
    onSearchUsers: suspend (String) -> List<User>,
    onUserSelect: (User) -> Unit,
    onWaterBubbleClick: (FriendStory) -> Unit,
    onSpeechBubbleClick: (FriendStory) -> Unit,
    modifier: Modifier = Modifier,
    onExpandedChanged: (Boolean) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isExpanded) {
        onExpandedChanged(isExpanded)
    }

    Box(modifier = modifier) {
        Column {
            // Friend List Filter
            FriendListFilter(
                options = filterOptions,
                selectedOptionId = selectedFilterId,
                onOptionSelected = onFilterSelected,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Drag handler area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = {
                                isDragging = false
                                if (abs(dragOffset) > 100f) {
                                    isExpanded = dragOffset < 0
                                }
                                dragOffset = 0f
                            },
                            onDragCancel = {
                                isDragging = false
                                dragOffset = 0f
                            },
                            onDrag = { change: PointerInputChange, dragAmount: Offset ->
                                change.consume()
                                dragOffset += dragAmount.y
                            }
                        )
                    }
            )

            // Friends list
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AddFriendItem(
                        searchUsers = { query ->
                            scope.async {
                                onSearchUsers(query)
                            }.await()
                        },
                        onUserSelect = onUserSelect
                    )
                }

                items(if (isExpanded) friends else friends.take(8)) { friend ->
                    FriendStoryItem(
                        friend = friend,
                        onWaterBubbleClick = onWaterBubbleClick,
                        onSpeechBubbleClick = onSpeechBubbleClick
                    )
                }
            }

            // More/Collapse button
            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = if (isExpanded) "접기" else "더보기",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
