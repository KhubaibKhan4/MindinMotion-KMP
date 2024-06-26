package org.mind.app.presentation.ui.screens.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import mind_in_motion.composeapp.generated.resources.Res
import mind_in_motion.composeapp.generated.resources.avatar
import mind_in_motion.composeapp.generated.resources.ic_logo
import org.jetbrains.compose.resources.Resource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.mind.app.domain.model.message.Message
import org.mind.app.domain.model.users.Users
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.components.EmptyChatPlaceholder
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.InfoDialog
import org.mind.app.presentation.ui.components.LoadingBox
import org.mind.app.presentation.ui.components.TypewriterEffect
import org.mind.app.presentation.ui.components.parseMessageText
import org.mind.app.presentation.ui.tabs.home.HomeTab
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark
import org.mind.app.utils.Constant

class ChatBotScreen : Screen {
    @Composable
    override fun Content() {
        ChatScreenContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenContent(viewModel: MainViewModel = koinInject()) {
    LocalPreferenceProvider {
        val preferences = LocalPreference.current
        val email by remember { mutableStateOf(preferences.getString("email")) }
        val messages by viewModel.messages.collectAsState()
        var userInput by remember { mutableStateOf("") }
        var currentUserId by remember { mutableStateOf<Users?>(null) }
        var isInfo by remember { mutableStateOf(false) }
        val isDark by LocalThemeIsDark.current
        val navigator = LocalTabNavigator.current
        LaunchedEffect(Unit) {
            viewModel.getUserByEmail(email.toString())
        }
        val currentUserState by viewModel.userByEmail.collectAsState()
        when (currentUserState) {
            is ResultState.Error -> {
                val error = (currentUserState as ResultState.Error).message
                ErrorBox(error)
            }

            ResultState.Loading -> {
                LoadingBox()
            }

            is ResultState.Success -> {
                val response = (currentUserState as ResultState.Success).data
                currentUserId = response
            }
        }
        LaunchedEffect(currentUserId) {
            if (currentUserId?.id != null) {
                viewModel.fetchBotMessages(currentUserId = currentUserId?.id.toString())
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Chat Bot") },
                    navigationIcon = {
                        Icon(
                            Icons.Default.ArrowBackIosNew, contentDescription = null,
                            tint = if (isDark) Color.White else Color.Black,
                            modifier = Modifier.clickable {
                                navigator.current = HomeTab
                            }
                        )
                    },
                    actions = {
                        Icon(
                            Icons.Default.Info, contentDescription = null,
                            tint = if (isDark) Color.White else Color.Black,
                            modifier = Modifier.clickable {
                                isInfo = !isInfo
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
                    .padding(start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    if (messages.isEmpty()) {
                        EmptyChatPlaceholder()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            reverseLayout = true
                        ) {
                            items(messages.reversed()) { message ->
                                currentUserId?.let {
                                    MessageBubble(message,it)
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                        placeholder = { Text("Type a message...") },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (userInput.isNotBlank()) {
                                        viewModel.sendMessage(
                                            userInput,
                                            currentUserId = currentUserId?.id.toString()
                                        )
                                        userInput = ""
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = if (isDark) Color.White else Color.Black
                                )
                            }
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Color.Blue,
                            focusedBorderColor = Color.DarkGray,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                }
            }
            if (isInfo) {
                InfoDialog(
                    onCloseClicked = { isInfo = false }
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    users: Users,
) {
    val isDark by LocalThemeIsDark.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUserMessage) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!message.isUserMessage) {
            Image(
                painter = painterResource(Res.drawable.ic_logo),
                contentDescription = "Bot Profile",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(30.dp)
                    .border(1.dp, if (isDark) Color.White else Color.Black, CircleShape)
            )
        }
        Box(
            modifier = Modifier
                .widthIn(max = 250.dp)
                .background(
                    color = if (message.isUserMessage) Color.Blue else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            if (message.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                val styledText = parseMessageText(message.text)
                if (message.showTypewriterEffect) {
                    TypewriterEffect(
                        text = styledText,
                        modifier = Modifier.padding(8.dp),
                        typingDelay = 50L,
                        blinkDelay = 500L
                    )
                } else {
                    Text(
                        text = styledText,
                        modifier = Modifier.padding(8.dp),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
        if (message.isUserMessage) {
            if (users.profileImage?.contains("null") == true) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(width = 1.dp, color = Color.Gray, shape = CircleShape)
                ) {
                    Text(
                        text = users.fullName.first().toString(),
                        modifier = Modifier.align(Alignment.Center),
                        color = if (isDark) Color.White else Color.Black,
                        fontSize = 24.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(width = 1.dp, color = Color.Gray, shape = CircleShape)
                ) {
                    val image: io.kamel.core.Resource<Painter> = asyncPainterResource(Constant.BASE_URL + users.profileImage)
                    KamelImage(
                        resource = image,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                            .clip(CircleShape)
                            .border(width = 1.dp, color = Color.Gray, shape = CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

            }
        }
    }
}