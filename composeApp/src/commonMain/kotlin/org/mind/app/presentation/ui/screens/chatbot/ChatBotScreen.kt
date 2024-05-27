package org.mind.app.presentation.ui.screens.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import kotlinx.coroutines.delay
import mind_in_motion.composeapp.generated.resources.Res
import mind_in_motion.composeapp.generated.resources.avatar
import mind_in_motion.composeapp.generated.resources.ic_cyclone
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.mind.app.domain.model.message.Message
import org.mind.app.presentation.ui.tabs.home.HomeTab
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark

class ChatBotScreen : Screen {
    @Composable
    override fun Content() {
        ChatScreenContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenContent(viewModel: MainViewModel = koinInject()) {
    val messages by viewModel.messages.collectAsState()
    var userInput by remember { mutableStateOf("") }
    val isDark by LocalThemeIsDark.current
    val navigator = LocalTabNavigator.current

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
                            MessageBubble(message)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = if (isDark) Color.White else Color.Black,
                            modifier = Modifier.clickable {
                                if (userInput.isNotBlank()) {
                                    viewModel.sendMessage(userInput)
                                    userInput = ""
                                }
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
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
                painter = painterResource(Res.drawable.ic_cyclone),
                contentDescription = "Bot Profile",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp),
                colorFilter = if (isDark) ColorFilter.tint(Color.White) else null
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
            Image(
                painter = painterResource(Res.drawable.avatar),
                contentDescription = "User Profile",
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 8.dp)
                    .clip(CircleShape)
            )
        }
    }
}

fun parseMessageText(text: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = text.split("\n")
        var isCodeBlock = false
        lines.forEachIndexed { index, line ->
            var formattedLine = line.replace("**", "").replace("*", "")
            if (index != lines.lastIndex) {
                formattedLine += "\n"
            }

            when {
                formattedLine.startsWith("### ") -> {
                    val heading = formattedLine.removePrefix("### ")
                    append(heading)
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        start = length - heading.length,
                        end = length
                    )
                }

                formattedLine.startsWith("## ") -> {
                    val heading = formattedLine.removePrefix("## ")
                    append(heading)
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        start = length - heading.length,
                        end = length
                    )
                }

                formattedLine.startsWith("# ") -> {
                    val heading = formattedLine.removePrefix("# ")
                    append(heading)
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        start = length - heading.length,
                        end = length
                    )
                }

                formattedLine.startsWith("***") && formattedLine.endsWith("***") -> {
                    val boldItalicText = formattedLine.removeSurrounding("***")
                    append(boldItalicText)
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        ),
                        start = length - boldItalicText.length,
                        end = length
                    )
                }

                formattedLine.startsWith("**") && formattedLine.endsWith("**") -> {
                    val boldText = formattedLine.removeSurrounding("**")
                    append(boldText)
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        ),
                        start = length - boldText.length,
                        end = length
                    )
                }

                formattedLine.startsWith("*") && formattedLine.endsWith("*") -> {
                    val italicText = formattedLine.removeSurrounding("*")
                    append(italicText)
                    addStyle(
                        style = SpanStyle(
                            fontStyle = FontStyle.Italic
                        ),
                        start = length - italicText.length,
                        end = length
                    )
                }

                formattedLine.startsWith("~~") && formattedLine.endsWith("~~") -> {
                    val strikethroughText = formattedLine.removeSurrounding("~~")
                    append(strikethroughText)
                    addStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.LineThrough
                        ),
                        start = length - strikethroughText.length,
                        end = length
                    )
                }

                formattedLine.startsWith("• ") -> {
                    append(formattedLine)
                }

                formattedLine.startsWith("* ") -> {
                    val bulletPoint = formattedLine.removePrefix("* ")
                    append("• $bulletPoint")
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Normal
                        ),
                        start = length - bulletPoint.length - 2,
                        end = length
                    )
                }

                formattedLine.startsWith("- [x] ") -> {
                    val taskText = formattedLine.removePrefix("- [x] ")
                    append("☑ $taskText")
                    addStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.LineThrough
                        ),
                        start = length - taskText.length - 2,
                        end = length
                    )
                }

                formattedLine.startsWith("- [ ] ") -> {
                    val taskText = formattedLine.removePrefix("- [ ] ")
                    append("☐ $taskText")
                }

                formattedLine.matches(Regex("^\\d+\\. .*")) -> {
                    append(formattedLine)
                }

                formattedLine.contains("• ") -> {
                    val parts = formattedLine.split("• ")
                    val bulletPoint = parts[0] + "• "
                    val boldText = parts[1]
                    append(bulletPoint)
                    append(boldText)
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        ),
                        start = length - boldText.length,
                        end = length
                    )
                }
                formattedLine.startsWith("```") && formattedLine.endsWith("```") -> {
                    val codeText = formattedLine.removeSurrounding("```")
                    append(codeText)
                    addStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = Color.Red,
                        ),
                        start = length - codeText.length,
                        end = length
                    )
                }

                else -> {
                    append(formattedLine)
                }
            }
            isCodeBlock = isCodeBlock || formattedLine.startsWith("```")
            isCodeBlock = isCodeBlock && !formattedLine.endsWith("```")
        }
    }
}

@Composable
fun TypewriterEffect(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    typingDelay: Long = 50L,
    blinkDelay: Long = 500L,
) {
    var visibleText by remember { mutableStateOf(AnnotatedString("")) }
    var isTypingFinished by remember { mutableStateOf(false) }

    LaunchedEffect(text) {
        text.forEachIndexed { index, _ ->
            visibleText = text.subSequence(0, index + 1) as AnnotatedString
            delay(typingDelay)
        }
        isTypingFinished = true
    }

    LaunchedEffect(isTypingFinished) {
        while (!isTypingFinished) {
            delay(blinkDelay)
            visibleText = if (visibleText.isNotEmpty() && visibleText.text.last() == '█') {
                AnnotatedString(
                    visibleText.text.dropLast(1),
                    visibleText.spanStyles,
                    visibleText.paragraphStyles
                )
            } else {
                AnnotatedString(
                    visibleText.text + "█",
                    visibleText.spanStyles,
                    visibleText.paragraphStyles
                )
            }
        }
    }

    Text(
        text = if (isTypingFinished) visibleText else AnnotatedString(
            visibleText.text + "█",
            visibleText.spanStyles,
            visibleText.paragraphStyles
        ),
        modifier = modifier,
        color = Color.White,
        fontSize = 16.sp
    )
}

@Composable
fun EmptyChatPlaceholder() {
    val isDark by LocalThemeIsDark.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_cyclone),
            contentDescription = "Empty Chat",
            modifier = Modifier.size(120.dp),
            tint = if (isDark) Color.White else Color.Black
        )
        Text(
            text = "No messages yet. Start the conversation!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = if (isDark) Color.White else Color.Black,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}