package org.mind.app.presentation.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.mind.app.domain.model.users.Users
import org.mind.app.presentation.ui.screens.chat.ChatScreen
import org.mind.app.theme.LocalThemeIsDark
import org.mind.app.utils.Constant

class AllUsersScreen(
    private val users: List<Users>,
) : Screen {
    @Composable
    override fun Content() {
        AllUsersContent(users)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllUsersContent(users: List<Users>) {
    val navigator = LocalNavigator.current
    val isDark by LocalThemeIsDark.current
    var searchQuery by remember { mutableStateOf("") }
    var isSearchEnable by remember { mutableStateOf(false) }
    val filteredUsersItems = if (searchQuery.isNotEmpty()) {
        users.filter { it.fullName.contains(searchQuery, ignoreCase = true) }
    } else {
        users
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("", fontWeight = FontWeight.Bold) },
                actions = {
                    if (isSearchEnable) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
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
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = null,
                                    modifier = Modifier.clickable {
                                        searchQuery = ""
                                        isSearchEnable = !isSearchEnable
                                    }
                                )
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
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                isSearchEnable = !isSearchEnable
                            }
                        )
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                           navigator?.pop()
                        }
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = it.calculateTopPadding(), start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredUsersItems) { usersItem ->
                    AllUserUiItem(usersItem)
                }
            }
        }
    }
}

@Composable
fun AllUserUiItem(user: Users) {
    val navigator = LocalNavigator.current
    Column(
        modifier = Modifier.fillMaxWidth()
            .clickable {
              navigator?.push(UserProfileScreen(user))
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
                    resource = asyncPainterResource(Constant.BASE_URL + user.profileImage),
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
                    text = user.country, style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
            }
            Spacer(modifier = Modifier.weight(1f))

        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}