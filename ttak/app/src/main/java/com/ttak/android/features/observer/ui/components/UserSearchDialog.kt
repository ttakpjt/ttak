package com.ttak.android.features.observer.ui.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.ttak.android.R
import com.ttak.android.domain.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UserSearchDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onUserSelect: (User) -> Unit,
    searchUsers: (String) -> Unit,
    searchResults: List<User>,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isVisible) {
        if (isVisible) {
            searchQuery = ""
        }
    }

    val performSearch = {
        if (searchQuery.length <= 20) {
            searchJob?.cancel()
            searchJob = scope.launch {
                try {
                    isLoading = true
                    Log.d("UserSearchDialog", "Starting search for query: $searchQuery")
                    delay(300)
                    searchUsers(searchQuery)
                    isLoading = false
                    focusManager.clearFocus()
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
        Box(
            modifier = modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.5f)
        ) {
            // Gradient Background with Blur Effects
            Canvas(modifier = Modifier.matchParentSize()) {
                val colors = listOf(
                    Color(0xFF2E1065),
                    Color(0xFF1E1B4B),
                    Color(0xFF172554)
                )
                val brush = Brush.verticalGradient(colors)
                drawRect(brush = brush)
            }

            // Blur Effects
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(x = 150.dp, y = (-50).dp)
                    .background(
                        Color(0x33A855F7),
                        CircleShape
                    )
                    .blur(radius = 70.dp)
            )

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(x = (-50).dp, y = 150.dp)
                    .background(
                        Color(0x333B82F6),
                        CircleShape
                    )
                    .blur(radius = 70.dp)
            )

            // Main Content
            Surface(
                modifier = Modifier
                    .matchParentSize(),
                color = Color.Transparent,
                shape = RoundedCornerShape(16.dp)
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
                            style = MaterialTheme.typography.titleMedium,
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { query ->
                                if (query.length <= 20) {
                                    searchQuery = query
                                }
                            },
                            modifier = Modifier
                                .weight(1f),
                            placeholder = { Text("닉네임을 입력해주세요", color = Color.Gray) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.White
                                )
                            },
                            trailingIcon = if (searchQuery.isNotEmpty()) {
                                {
                                    IconButton(
                                        onClick = { searchQuery = "" }
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear search",
                                            tint = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            } else null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if (searchQuery.isNotBlank()) {
                                        performSearch()
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0x1AFFFFFF),
                                unfocusedContainerColor = Color(0x1AFFFFFF),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White.copy(alpha = 0.5f),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isLoading
                        )
                    }

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
                                val filteredResults = searchResults.filter { it.relation != "Self" }
                                if (filteredResults.isEmpty()) {
                                    Text(
                                        text = "검색 결과가 없습니다",
                                        color = Color.Gray,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(16.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(
                                            items = filteredResults,
                                            key = { user -> user.userId }
                                        ) { user ->
                                            UserSearchItem(
                                                user = user,
                                                onUserSelect = onUserSelect
                                            )
                                        }
                                    }
                                }
                            }
                            searchQuery.isNotBlank() && !isLoading -> {
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
}

@Composable
private fun UserSearchItem(
    user: User,
    onUserSelect: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
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

        if (user.relation != "Friend") {
            Button(
                onClick = { onUserSelect(user) },
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
        } else {
            Text(
                text = "친구",
                color = Color.Gray,
                modifier = Modifier.padding(end = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}