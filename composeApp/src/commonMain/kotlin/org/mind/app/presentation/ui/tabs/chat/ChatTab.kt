package org.mind.app.presentation.ui.tabs.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.mind.app.presentation.ui.screens.chatbot.ChatBotScreen

object ChatTab : Tab {
    @Composable
    override fun Content() {
        Navigator(ChatBotScreen())
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.ChatBubble)
            val title = "Chat Bot"
            val index: UShort = 3u
            return TabOptions(index, title, icon)
        }
}