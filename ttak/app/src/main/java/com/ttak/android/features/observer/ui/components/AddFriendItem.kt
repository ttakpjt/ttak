package com.ttak.android.features.observer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ttak.android.R
import com.ttak.android.domain.model.User

@Composable
fun AddFriendItem(
    searchUsers: (String) -> Unit,  // suspend 제거
    searchResults: List<User>,      // 검색 결과 추가
    onUserSelect: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSearchModal by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (showSearchModal) {
        UserSearchDialog(
            isVisible = true,
            onDismiss = { showSearchModal = false },
            searchUsers = searchUsers,
            searchResults = searchResults,  // 검색 결과 전달
            onUserSelect = { user ->
                onUserSelect(user)
                showSearchModal = false
            }
        )
    }

    Column(
        modifier = modifier.clickable { showSearchModal = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = CircleShape
                )
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.add_friend_icon),
                contentDescription = "Add friend",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}