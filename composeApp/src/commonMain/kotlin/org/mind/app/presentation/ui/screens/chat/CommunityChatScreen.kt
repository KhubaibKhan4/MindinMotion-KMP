package org.mind.app.presentation.ui.screens.chat

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.mind.app.createTempFileFromBitmap
import org.mind.app.domain.model.community.CommunityMessage
import org.mind.app.domain.model.users.Users
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.LoadingBox
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark
import org.mind.app.utils.Constant.BASE_URL
import org.mind.app.utils.formatTimestampToHumanReadable
import kotlin.time.Duration.Companion.seconds

class CommunityChatScreen(
    private val communityId: String,
) : Screen {
    @Composable
    override fun Content() {
        CommunityChatScreenContent(communityId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityChatScreenContent(
    communityId: String,
    viewModel: MainViewModel = koinInject(),
) {
    val community by viewModel.getCommunity(communityId).collectAsState(initial = null)
    val allMessages by viewModel.communityMessages.collectAsState()
    val messages = allMessages[communityId] ?: emptyList()
    val currentUserEmail = LocalPreference.current.getString("email") ?: ""
    var messageText by remember { mutableStateOf("") }
    val isDark by LocalThemeIsDark.current
    val navigator = LocalNavigator.current
    var expanded by remember { mutableStateOf(false) }
    var isDropdownMenuExpanded by remember { mutableStateOf(false) }


    val senderUsersState = remember { mutableStateMapOf<String, Users>() }

    LaunchedEffect(communityId) {
        viewModel.observeCommunityMessages(communityId)
    }

    LaunchedEffect(messages) {
        val senderEmails = messages.map { it.senderEmail }.distinct()
        viewModel.getUsersByEmails(senderEmails)
    }

    val usersByEmailsState by viewModel.usersByEmails.collectAsState()

    LaunchedEffect(usersByEmailsState) {
        if (usersByEmailsState is ResultState.Success) {
            senderUsersState.clear()
            (usersByEmailsState as ResultState.Success<List<Users>>).data.forEach { user ->
                senderUsersState[user.email] = user
            }
        }
    }

    LaunchedEffect(messages) {
        println("CommunityChatScreen Messages: ${messages.size}")
        messages.forEach { message ->
            println("CommunityChatScreen Message: ${message.message}, Sender: ${message.senderEmail}")
        }
    }

    LaunchedEffect(usersByEmailsState) {
        println("CommunityChatScreen User fetching result: $usersByEmailsState")
    }
    val scope = rememberCoroutineScope()
    var isUploadingImage by remember { mutableStateOf(false) }
    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            if (byteArrays.isNotEmpty()){
                byteArrays.firstOrNull()?.let { byteArray ->
                    isUploadingImage = true
                    val file = createTempFileFromBitmap(byteArray.toImageBitmap())
                    scope.launch {
                        val imageUrl =
                            viewModel.uploadImageAndGetUrl(imageBytes = file, currentUserEmail)
                        viewModel.sendImageMessage(currentUserEmail, currentUserEmail, file)
                        messageText = ""
                        delay(12.seconds)
                        isUploadingImage = false
                    }
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        community?.name ?: "Community Chat",
                        color = if (isDark) Color.White else Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = if (isDark) Color.White else Color.Black
                ),
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = null,
                            modifier = Modifier.clickable { navigator?.pop() }
                        )
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = community?.name?.first().toString(),
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White,
                                fontSize = 24.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            expanded = false
                            navigator?.push(CommunityDetailScreen(communityId = communityId))
                        }) {
                            Text("Community Details")
                        }
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding(), start = 4.dp, end = 4.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    reverseLayout = true
                ) {
                    items(messages.reversed()) { message ->
                        CommunityMessageItem(message, senderUsersState[message.senderEmail])
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Type your message...") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Attachment,
                                contentDescription = null,
                                modifier = Modifier.clickable { isDropdownMenuExpanded = !isDropdownMenuExpanded }
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .border(
                                1.dp,
                                if (isDark) Color.White else Color.Black,
                                RoundedCornerShape(16.dp)
                            ),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    if (isDropdownMenuExpanded) {
                        DropdownMenu(
                            expanded = isDropdownMenuExpanded,
                            onDismissRequest = { isDropdownMenuExpanded = false }
                        ) {
                            androidx.compose.material3.DropdownMenuItem(onClick = {
                                singleImagePicker.launch()
                                isDropdownMenuExpanded = false
                            },
                                text = { Text("Image") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null
                                    )
                                })
                            androidx.compose.material3.DropdownMenuItem(onClick = {
                                scope.launch {
                                    val file = FileKit.pickFile(
                                        type = PickerType.File(listOf("pdf")),
                                        mode = PickerMode.Single,
                                        title = "Pick an image",
                                    )
                                    if (file?.readBytes()?.isNotEmpty() == true) {
                                        file?.let { byteArray ->
                                            isUploadingImage = true
                                            val file = createTempFileFromBitmap(
                                                byteArray.readBytes().toImageBitmap()
                                            )
                                            scope.launch {
                                                val pdfUrl =
                                                    viewModel.uploadPdfAndGetUrl(
                                                        pdfBytes = file,
                                                        currentUserEmail
                                                    )
                                                viewModel.sendPdfMessage(
                                                    currentUserEmail,
                                                    currentUserEmail,
                                                    file
                                                )
                                                messageText = ""
                                                delay(12.seconds)
                                                isUploadingImage = false
                                            }
                                        }
                                    }
                                }
                                isDropdownMenuExpanded = false
                            },
                                text = { Text("PDF") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = null
                                    )
                                })
                        }
                    }

                    IconButton(onClick = {
                        viewModel.sendCommunityMessage(communityId, currentUserEmail, messageText)
                        messageText = ""
                    }) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (isUploadingImage) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    )
}
class CommunityDetailScreen(
    private val communityId: String,
) : Screen {
    @Composable
    override fun Content() {
        CommunityDetailContent(communityId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailContent(
    communityId: String,
    viewModel: MainViewModel = koinInject(),
) {
    val community by viewModel.getCommunity(communityId).collectAsState(initial = null)
    val communityUsers by viewModel.communityUsers.collectAsState()
    val nonCommunityUsers by viewModel.nonCommunityUsers.collectAsState()
    val currentUserEmail = LocalPreference.current.getString("email") ?: ""
    val isAdmin = community?.admin == currentUserEmail
    val navigator = LocalNavigator.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(communityId) {
        viewModel.fetchCommunityUsers(communityId)
    }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    Text("Community Details")
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier.clickable { navigator?.pop() }
                    )
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text(
                    text = community?.name ?: "Community",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )

                Text(
                    text = "Members",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(communityUsers) { user ->
                        MemberItem(user, isAdmin, currentUserEmail, scope, viewModel,communityId)
                    }
                }

                if (isAdmin) {
                    Text(
                        text = "Non-Members",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(nonCommunityUsers) { user ->
                            NonMemberItem(user, scope, viewModel, communityId)
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun MemberItem(
    user: Users,
    isAdmin: Boolean,
    currentUserEmail: String,
    scope: CoroutineScope,
    viewModel: MainViewModel,
    communityId: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.fullName.first().toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            if (user.email == currentUserEmail && isAdmin) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            if (isAdmin && user.email != currentUserEmail) {
                IconButton(onClick = {
                    scope.launch {
                        viewModel.removeUserFromCommunity(communityId, user.email)
                    }
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove User")
                }
            }
        }
    }
}

@Composable
private fun NonMemberItem(
    user: Users,
    scope: CoroutineScope,
    viewModel: MainViewModel,
    communityId: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.fullName.first().toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                scope.launch {
                    viewModel.addUserToCommunity(communityId, user.email)
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add User")
            }
        }
    }
}


@Composable
fun CommunityMessageItem(
    message: CommunityMessage,
    senderData: Users?,
    viewModel: MainViewModel = koinInject(),
) {
    val isDark by LocalThemeIsDark.current
    val currentUserEmail = LocalPreference.current.getString("email")
    val isSentByCurrentUser = message.senderEmail == currentUserEmail
    val alignment = if (isSentByCurrentUser) Alignment.End else Alignment.Start
    val formattedTime = formatTimestampToHumanReadable(message.timestamp)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (!isSentByCurrentUser) {
            if (senderData?.profileImage?.contains("null") != true) {
                KamelImage(
                    resource = asyncPainterResource(BASE_URL + senderData?.profileImage),
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
                        text = senderData?.fullName?.first().toString(),
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isSentByCurrentUser) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface,
                contentColor = if (isSentByCurrentUser) Color.White else if (isDark) Color.White else Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Column(
                horizontalAlignment = alignment,
                modifier = Modifier
                    .padding(8.dp)
                    .widthIn(max = 250.dp)
            ) {
                if (!isSentByCurrentUser) {
                    Text(
                        text = senderData?.fullName?.take(16).toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }
                if (message.message.isNotEmpty()) {
                    Text(
                        text = message.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isDark && isSentByCurrentUser) Color.White else if (isDark) Color.White else Color.Black,
                        modifier = Modifier.padding(4.dp)
                    )
                }else if (message.imageUrl?.isNotEmpty()==true){
                    val image : Resource<Painter> = asyncPainterResource(message.imageUrl.toString())
                    KamelImage(
                        resource = image,
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        onLoading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = {
                                        it
                                    },
                                )
                            }
                        },
                        onFailure = {
                            Text(
                                text = "Failed to load image",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isDark) Color.White else Color.Black,
                                modifier = Modifier.padding(4.dp))
                        }
                    )
                }else{
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            modifier = Modifier.size(55.dp)
                        )
                        Text(
                            text = "PDF File",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isDark && isSentByCurrentUser) Color.White else if (isDark) Color.White else Color.Black,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark && isSentByCurrentUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }
        }

        if (isSentByCurrentUser) {
            if (senderData?.profileImage?.contains("null") != true) {
                KamelImage(
                    resource = asyncPainterResource(BASE_URL + senderData?.profileImage),
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
                        text = senderData?.fullName?.first().toString(),
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}