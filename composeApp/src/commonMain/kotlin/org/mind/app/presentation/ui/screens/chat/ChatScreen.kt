package org.mind.app.presentation.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.koinInject
import org.mind.app.domain.model.chat.ChatMessage
import org.mind.app.domain.model.community.Community
import org.mind.app.domain.model.community.CommunityMessage
import org.mind.app.domain.model.users.Users
import org.mind.app.presentation.ui.screens.profile.ProfileScreen
import org.mind.app.presentation.ui.tabs.home.HomeTab
import org.mind.app.presentation.ui.tabs.profile.ProfileTab
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark
import org.mind.app.utils.Constant.BASE_URL
import org.mind.app.utils.formatTimestampToHumanReadable

class ChatScreen(
    private val users: List<Users>,
) : Screen {
    @Composable
    override fun Content() {
        ChatScreenContent(users)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenContent(
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

        val mergedList = users.sortedByDescending { user ->
            latestMessages[user.email]?.timestamp ?: Long.MIN_VALUE
        }.partition { user ->
            latestMessages.containsKey(user.email)
        }.let { (chattedUsers, newUsers) ->
            chattedUsers
        }
        val communities = viewModel.communities.collectAsState(initial = emptyList()).value

        val userCommunityIds = currentUser?.email
        val userCommunities = communities.filter { community ->
            community.members.contains(userCommunityIds)
        }

        LaunchedEffect(Unit) {
            viewModel.observeChatMessages()
        }


        Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = { Text("Inbox", color = if (isDark) Color.White else Color.Black) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = if (isDark) Color.White else Color.Black
                    ),
                    actions = {
                        if (currentUser?.profileImage?.contains("null") != true) {
                            KamelImage(
                                resource = asyncPainterResource(BASE_URL + currentUser?.profileImage),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .border(width = 1.dp, color = Color.Gray, shape = CircleShape)
                                    .clickable { localNavigator?.push(UserProfile()) },
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .border(
                                        width = 1.dp,
                                        color = if (isDark) Color.White else Color.Black,
                                        shape = CircleShape
                                    )
                                    .clickable { localNavigator?.push(UserProfile()) }
                            ) {
                                Text(
                                    text = currentUser?.fullName?.first().toString(),
                                    modifier = Modifier.align(Alignment.Center),
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    },
                    navigationIcon = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    navigator.current = HomeTab
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                )
            },
            floatingActionButton = {
                androidx.compose.material3.FloatingActionButton(
                    onClick = {
                        localNavigator?.push(AllChatUsers(allUsers = users))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                androidx.compose.material.TextField(
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
                    colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        textColor = if (isDark) Color.White else Color.Black,
                        backgroundColor = if (isDark) Color.DarkGray else Color.LightGray,
                        trailingIconColor = if (isDark) Color.LightGray else Color.Gray,
                        leadingIconColor = if (isDark) Color.LightGray else Color.Gray
                    )
                )
                Text(
                    text = "Communities",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (isDark) Color.White else Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (userCommunities.isEmpty()) {
                    Text(
                        text = "No communities available",
                        color = if (isDark) Color.White else Color.Black
                    )
                } else {
                    LazyColumn {
                        items(userCommunities.filter { community ->
                            community.name.contains(searchText.text, ignoreCase = true)
                        }) { community ->
                            CommunityItem(
                                community = community,
                                onClick = {
                                    localNavigator?.push(CommunityChatScreen(communityId = community.id))
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Chats",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (isDark) Color.White else Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

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
                        items(mergedList.filter { user ->
                            user.fullName.contains(searchText.text, ignoreCase = true)
                        }) { user ->
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

@Composable
fun ChatUIItem(
    user: Users,
    latestMessage: ChatMessage?,
    isNewMessage: Boolean,
    onClick: () -> Unit,
) {
    val navigator = LocalNavigator.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigator?.push(ChatDetailScreen(user))
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (user.profileImage?.contains("null") != true) {
                KamelImage(
                    resource = asyncPainterResource(BASE_URL + user.profileImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = user.fullName.first().toString(),
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(text = user.fullName.take(16), style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (latestMessage?.imageUrl?.isNotEmpty()==true){
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Photo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.LightGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 170.dp)
                            )
                        }
                    }else{
                        Text(
                            text = latestMessage?.message ?: "Chat Now...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 170.dp)
                        )
                    }
                    if (isNewMessage) {
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = latestMessage?.let { formatTimestampToHumanReadable(it.timestamp) } ?: "",
                style = MaterialTheme.typography.titleMedium
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}
@Composable
fun CommunityItem(community: Community, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = community.name.first().toString(),
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = community.name,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}