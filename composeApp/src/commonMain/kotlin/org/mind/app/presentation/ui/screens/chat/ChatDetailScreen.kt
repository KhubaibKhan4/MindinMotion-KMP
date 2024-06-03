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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.mind.app.createTempFileFromBitmap
import org.mind.app.domain.model.chat.ChatMessage
import org.mind.app.domain.model.users.Users
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark
import org.mind.app.utils.Constant.BASE_URL
import org.mind.app.utils.formatTimestampToHumanReadable
import kotlin.time.Duration.Companion.seconds

class ChatDetailScreen(
    private val users: Users,
) : Screen {
    @Composable
    override fun Content() {
        ChatDetailScreenContent(users)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreenContent(
    users: Users,
    viewModel: MainViewModel = koinInject(),
) {
    val currentUserEmail = LocalPreference.current.getString("email") ?: ""
    val allMessages by viewModel.chatMessages.collectAsState()
    val messages = allMessages.filter {
        (it.senderEmail == currentUserEmail && it.receiverEmail == users.email) ||
                (it.senderEmail == users.email && it.receiverEmail == currentUserEmail)
    }
    val scope = rememberCoroutineScope()
    val isDark by LocalThemeIsDark.current
    val navigator = LocalNavigator.current
    var messageText by remember { mutableStateOf("") }
    var isUploadingImage by remember { mutableStateOf(false) }

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let { byteArray ->
                isUploadingImage = true
                val file = createTempFileFromBitmap(byteArray.toImageBitmap())
                scope.launch {
                    val imageUrl =
                        viewModel.uploadImageAndGetUrl(imageBytes = file, currentUserEmail)
                    viewModel.sendImageMessage(currentUserEmail, users.email, file)
                    messageText = ""
                    delay(12.seconds)
                    isUploadingImage = false
                }
            }
        }
    )
    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    Text(
                        users.fullName.take(16),
                        color = if (isDark) Color.White else Color.Black
                    )
                },
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
                            modifier = Modifier.clickable { navigator?.pop() }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (users.profileImage?.contains("null") != true) {
                            KamelImage(
                                resource = asyncPainterResource(BASE_URL + users.profileImage),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, if (isDark) Color.White else Color.Black),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .border(
                                        1.dp,
                                        if (isDark) Color.White else Color.Black,
                                        CircleShape
                                    )
                            ) {
                                Text(
                                    text = users.fullName.first().toString(),
                                    modifier = Modifier.align(Alignment.Center),
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        modifier = Modifier.clickable { }
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding(), start = 8.dp, end = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    ChatMessageItem(message, users)
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
                            modifier = Modifier.clickable {
                                singleImagePicker.launch()
                            }
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

                IconButton(onClick = {
                    viewModel.sendMessageChat(currentUserEmail, users.email, messageText)
                    messageText = ""
                }) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                println("UploadImage isUploadingImage: $isUploadingImage")
                if (isUploadingImage) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun DateHeader(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = date,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage, users: Users) {
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
            if (users.profileImage?.contains("null") != true) {
                KamelImage(
                    resource = asyncPainterResource(BASE_URL + users.profileImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .border(1.dp, if (isDark) Color.White else Color.Black),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .border(1.dp, if (isDark) Color.White else Color.Black, CircleShape)
                ) {
                    Text(
                        text = users.fullName.first().toString(),
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
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
                if (message.message.isNotEmpty()) {
                    Text(
                        text = message.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isDark && isSentByCurrentUser) Color.White else if (isDark) Color.White else Color.Black,
                        modifier = Modifier.padding(4.dp)
                    )
                } else {
                    val image: Resource<Painter> = asyncPainterResource(message.imageUrl.toString())
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
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}