package org.mind.app.presentation.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import org.koin.compose.koinInject
import org.mind.app.domain.model.users.Users
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark

class AllChatUsers(
    private val allUsers: List<Users>,
) : Screen {
    @Composable
    override fun Content() {
        AllChatUsersContent(allUsers)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllChatUsersContent(
    users: List<Users>,
    viewModel: MainViewModel = koinInject(),
) {
    var lastClickedChatUserTimestamp by remember { mutableStateOf<Pair<String, Long>?>(null) }
    LocalPreferenceProvider {
        val preference = LocalPreference.current
        val currentUserEmail by remember { mutableStateOf(preference.getString("email")) }
        var searchText by remember { mutableStateOf(TextFieldValue("")) }

        val currentUser by remember { mutableStateOf(users.find { it.email == currentUserEmail }) }
        val navigator = LocalTabNavigator.current
        val localNavigator = LocalNavigator.current
        val isDark by LocalThemeIsDark.current

        val latestMessages = viewModel.chatMessages.collectAsState(initial = emptyList()).value
            .filter { message ->
                (message.senderEmail == currentUserEmail || message.receiverEmail == currentUserEmail)
            }
            .groupBy { message ->
                if (message.senderEmail == currentUserEmail) message.receiverEmail
                else message.senderEmail
            }
            .mapValues { entry -> entry.value.maxByOrNull { it.timestamp } }

        val filteredUsers = users.filter { user ->
            user.fullName.contains(
                searchText.text,
                ignoreCase = true
            ) && user.email != currentUserEmail
        }

        val sortedUsers = filteredUsers.sortedByDescending { user ->
            val latestMessage = latestMessages[user.email]
            latestMessage?.timestamp ?: Long.MIN_VALUE
        }

        val mergedList = sortedUsers


        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("All Users", color = if (isDark) Color.White else Color.Black) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = if (isDark) Color.White else Color.Black
                    ),
                    navigationIcon = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    localNavigator?.pop()
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    },
                    actions = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                localNavigator?.push(CreateCommunityScreen(users))
                            }
                        )
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = {
                        Text(
                            "Search...",
                            color = if (isDark) Color.LightGray else Color.Gray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        AnimatedVisibility(searchText.text.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    searchText = TextFieldValue("")
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(4.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        textColor = if (isDark) Color.White else Color.Black,
                        backgroundColor = if (isDark) Color.DarkGray else Color.LightGray,
                        trailingIconColor = if (isDark) Color.LightGray else Color.Gray,
                        leadingIconColor = if (isDark) Color.LightGray else Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (mergedList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchText.text.isEmpty()) {
                                "No chats available"
                            } else {
                                "No results found"
                            },
                            color = if (isDark) Color.White else Color.Black,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                } else {
                    LazyColumn {
                        items(mergedList) { user ->
                            val latestMessage = latestMessages[user.email]
                            val isNewMessage =
                                latestMessage != null && latestMessage.receiverEmail == currentUserEmail
                            val isClicked = lastClickedChatUserTimestamp?.first == user.email
                                    && lastClickedChatUserTimestamp?.second == latestMessage?.timestamp

                            ChatUIItem(
                                user = user,
                                latestMessage = latestMessage,
                                isNewMessage = isNewMessage && !isClicked,
                                onClick = {
                                    lastClickedChatUserTimestamp =
                                        user.email to (latestMessage?.timestamp ?: 0L)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
