package com.ttak.android.features.observer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.ttak.android.data.model.User
import kotlinx.coroutines.*
import com.ttak.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onUserSelect: (User) -> Unit,
    searchUsers: suspend (String) -> List<User>,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchResults by remember { mutableStateOf(emptyList<User>()) }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()

    // 검색 결과를 위한 state holder
    val searchState = remember {
        mutableStateOf<SearchState>(SearchState.Initial)
    }

    DisposableEffect(Unit) {
        onDispose {
            searchJob?.cancel()
        }
    }

    val performSearch = { query: String ->
        // 20자 초과시 아무 것도 하지 않음
        if (query.length <= 20) { // 20자 이하일 때만 검색 실행
            searchJob?.cancel()
            searchJob = scope.launch(Dispatchers.IO) {
                try {
                    if (query.isBlank()) {
                        withContext(Dispatchers.Main) {
                            searchState.value = SearchState.Initial
                            searchResults = emptyList()
                        }
                        return@launch
                    }

                    withContext(Dispatchers.Main) {
                        isLoading = true
                    }

                    delay(300) // 디바운스

                    val results = searchUsers(query)

                    withContext(Dispatchers.Main) {
                        searchResults = results
                        searchState.value = if (results.isEmpty()) {
                            SearchState.Empty
                        } else {
                            SearchState.Success(results)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        searchState.value = SearchState.Error("검색 중 오류가 발생했습니다")
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        isLoading = false
                    }
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
                        performSearch(query)
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
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                    searchResults = emptyList()
                                    searchState.value = SearchState.Initial
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = Color.Black
                                )
                            }
                        }
                    } else null,
                    singleLine = true,
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
                        searchResults.isEmpty() && searchQuery.isNotBlank() -> {
                            Text(
                                text = "검색 결과가 없습니다",
                                color = Color.Gray,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(
                                    items = searchResults,
                                    key = { it.id }
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
                    }
                }
            }
        }
    }
}

sealed class SearchState {
    object Initial : SearchState()
    object Empty : SearchState()
    data class Success(val results: List<User>) : SearchState()
    data class Error(val message: String) : SearchState()
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
                model = user.profileImageUrl,
                contentDescription = "Profile image of ${user.name}",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = user.name,
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