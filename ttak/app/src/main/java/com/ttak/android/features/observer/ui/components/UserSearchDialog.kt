package com.ttak.android.features.observer.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.ttak.android.domain.model.User
import kotlinx.coroutines.*
import com.ttak.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onUserSelect: (User) -> Unit,
    searchUsers: (String) -> Unit,
    searchResults: List<User>,  // val parameter
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val performSearch = { query: String ->
        if (query.length <= 20) {
            searchJob?.cancel()
            searchJob = scope.launch {
                try {
                    if (query.isBlank()) {
                        return@launch
                    }

                    isLoading = true
                    Log.d("UserSearchDialog", "Starting search for query: $query")

                    delay(300)
                    searchUsers(query)  // ViewModel의 searchUsers 호출

                    isLoading = false
                } catch (e: Exception) {
                    Log.e("UserSearchDialog", "Search error", e)
                    isLoading = false
                }
            }
        }
    }

    Dialog(
        onDismissRequest = {
            searchJob?.cancel()
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.5f),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF2F2F32)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "친구 검색",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = {
                            searchJob?.cancel()
                            onDismiss()
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    placeholder = { Text("닉네임을 입력해주세요") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Black
                        )
                    },
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        {
                            Row {
                                IconButton(
                                    onClick = {
                                        searchQuery = ""  // 검색어만 지우기
                                        searchUsers("")   // 빈 검색어로 검색하여 결과 초기화
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear search",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    } else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            performSearch(searchQuery)
                            focusManager.clearFocus()  // 키보드 닫기
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFD9D9D9),
                        unfocusedContainerColor = Color(0xFFD9D9D9),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                )

                // Results or Loading
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(36.dp)
                                    .align(Alignment.Center),
                                color = Color.White
                            )
                        }
                        searchResults.isNotEmpty() -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(
                                    items = searchResults,
                                    key = { it.userId }
                                ) { user ->
                                    UserSearchItem(
                                        user = user,
                                        onUserSelect = {
                                            searchJob?.cancel()
                                            onUserSelect(user)
                                        }
                                    )
                                }
                            }
                        }
                        searchQuery.isNotBlank() -> {
                            Text(
                                text = "검색 결과가 없습니다",
                                color = Color.Gray,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserSearchItem(
    user: User,
    onUserSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(64.dp)
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.userImg,
                contentDescription = "Profile image of ${user.userName}",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = user.userName,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }

        Button(
            onClick = onUserSelect,
            modifier = Modifier
                .height(32.dp)
                .width(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7FEC93)
            ),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.plus_icon),
                contentDescription = "Add user",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}