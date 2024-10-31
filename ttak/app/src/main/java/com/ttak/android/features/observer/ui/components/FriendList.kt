package com.ttak.android.features.observer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ttak.android.data.model.FriendStory

@Composable
fun FriendList(
    friends: List<FriendStory>,
    onAddFriendClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AddFriendItem(onAddFriendClick = onAddFriendClick)
        }
        items(friends) { friend ->
            FriendStoryItem(friend = friend)
        }
    }
}

