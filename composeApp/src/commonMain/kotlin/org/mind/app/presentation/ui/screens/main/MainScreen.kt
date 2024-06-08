package org.mind.app.presentation.ui.screens.main

import NotesTab
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.benasher44.uuid.Uuid
import com.example.cmppreference.LocalPreference
import org.mind.app.presentation.ui.components.TabItem
import org.mind.app.presentation.ui.screens.auth.login.LoginScreen
import org.mind.app.presentation.ui.screens.auth.reset.ResetPasswordScreen
import org.mind.app.presentation.ui.screens.auth.signup.SignupScreen
import org.mind.app.presentation.ui.tabs.chat.ChatDetailTab
import org.mind.app.presentation.ui.tabs.chat.ChatTab
import org.mind.app.presentation.ui.tabs.home.HomeTab
import org.mind.app.presentation.ui.tabs.profile.ProfileTab
import org.mind.app.presentation.ui.tabs.quiz.QuizTab
import org.mind.app.presentation.ui.tabs.resume.ResumeTab
import kotlin.random.Random

class MainScreen(
    private val uniqueKey : String
) : Tab {
    override val key: ScreenKey= uniqueKey
    @Composable
    override fun Content() {
        val preference = LocalPreference.current
        TabNavigator(
            tab = HomeTab,
            disposeNestedNavigators = false,
        ) { tabNavigator ->
            Scaffold(bottomBar = {
                if (
                    tabNavigator.current != LoginScreen &&
                    tabNavigator.current != ChatTab &&
                    tabNavigator.current != ChatDetailTab &&
                    tabNavigator.current != SignupScreen &&
                    tabNavigator.current != ResetPasswordScreen
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.ime),
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = contentColorFor(Color.Red),
                        tonalElevation = 16.dp
                    ) {
                        TabItem(HomeTab)
                        TabItem(QuizTab)
                        TabItem(NotesTab)
                        TabItem(ResumeTab)
                        TabItem(ProfileTab)
                    }
                }
            }) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(
                        bottom = it.calculateBottomPadding(),
                        start = 0.dp
                    )
                ) {
                    CurrentTab()
                }
            }
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Fireplace)
            val title = "Main"
            val index: UShort = Random.nextInt(10).toUShort()
            return remember {
                TabOptions(
                    index = index,
                    title = title,
                    icon = icon
                )
            }
        }
}