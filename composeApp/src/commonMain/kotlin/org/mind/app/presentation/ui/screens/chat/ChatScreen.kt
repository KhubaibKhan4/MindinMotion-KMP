package org.mind.app.presentation.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.mind.app.domain.model.users.Users
import org.mind.app.presentation.ui.tabs.home.HomeTab
import org.mind.app.theme.LocalThemeIsDark
import org.mind.app.utils.Constant.BASE_URL

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
fun ChatScreenContent(users: List<Users>) {
    LocalPreferenceProvider {
        val preference = LocalPreference.current
        val currentUserEmail by remember { mutableStateOf(preference.getString("email")) }
        var searchText by remember { mutableStateOf(TextFieldValue("")) }

        val filteredUsers = users.filter { user ->
            user.fullName.contains(
                searchText.text,
                ignoreCase = true
            ) && user.email != currentUserEmail
        }
        val currentUser by remember { mutableStateOf(users.find { it.email == currentUserEmail }) }
        val navigator = LocalTabNavigator.current
        val isDark by LocalThemeIsDark.current
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
                                    .border(
                                        width = 1.dp,
                                        color = if (isDark) Color.White else Color.Black
                                    ),
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
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(filteredUsers) { user ->
                        ChatUIItem(user)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatUIItem(user: Users) {
    val navigator = LocalNavigator.current
    Column(
        modifier = Modifier.fillMaxWidth()
            .clickable {
                navigator?.push(ChatDetailScreen(user))
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
                Text(
                    text = "How are you?", style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "12:00", style = MaterialTheme.typography.titleMedium)
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}