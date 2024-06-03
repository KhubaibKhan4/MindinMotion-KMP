package org.mind.app.presentation.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.Checkbox
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.koinInject
import org.mind.app.domain.model.users.Users
import org.mind.app.notify
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.utils.Constant

class CreateCommunityScreen(
    private val users: List<Users>,
) : Screen {
    @Composable
    override fun Content() {
        LocalPreferenceProvider {
            CommunityScreenContent(users)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreenContent(
    users: List<Users>,
    viewModel: MainViewModel = koinInject()
) {
    val preference = LocalPreference.current
    val currentUser by remember { mutableStateOf(preference.getString("email")) }
    var communityName by remember { mutableStateOf("") }
    val selectedMembers = remember { mutableStateListOf<String>() }
    var showError by remember { mutableStateOf(false) }
    val localNavigator = LocalNavigator.current
    var isNotifyEnabled by remember { mutableStateOf(false) }

    if (currentUser != null && !selectedMembers.contains(currentUser)) {
        selectedMembers.add(currentUser!!)
    }

    val communityState by viewModel.communities.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Community") },
                navigationIcon = {
                    IconButton(onClick = { localNavigator?.pop() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                OutlinedTextField(
                    value = communityName,
                    onValueChange = {
                        communityName = it
                        showError = false
                    },
                    label = { Text("Community Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    isError = showError
                )
                if (showError) {
                    Text(
                        text = "Community name cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                }
            }

            item {
                Text("Select Members", modifier = Modifier.padding(bottom = 8.dp))
            }

            items(users) { user ->
                CommunityUsers(
                    user = user,
                    selectedMembers = selectedMembers,
                    currentUser = currentUser.toString()
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Selected Members", modifier = Modifier.padding(bottom = 8.dp))
            }

            items(selectedMembers) { email ->
                val user = users.find { it.email == email }
                if (user != null) {
                    Text(text = user.fullName, style = MaterialTheme.typography.bodyMedium)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Button(
                    onClick = {
                        if (communityName.isEmpty()) {
                            showError = true
                        } else {
                            viewModel.createCommunity(
                                name = communityName,
                                members = selectedMembers,
                                admin = currentUser.toString()
                            )
                            isNotifyEnabled = true
                            localNavigator?.pop()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Community")
                }
                if (isNotifyEnabled){
                    notify("Community created successfully")
                }
            }


            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Communities", modifier = Modifier.padding(bottom = 8.dp))
            }

            items(communityState) { community ->
                Text(text = community.name, style = MaterialTheme.typography.bodyMedium)
            }
        }

    }
}

@Composable
fun CommunityUsers(
    user: Users,
    selectedMembers: MutableList<String>,
    currentUser: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = selectedMembers.contains(user.email),
            onCheckedChange = {
                if (it) selectedMembers.add(user.email)
                else selectedMembers.remove(user.email)
            }
        )
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.username ?: "Chat Now...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 170.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (user.email == currentUser) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = "Admin",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
}