package org.mind.app.presentation.ui.tabs.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import org.koin.compose.koinInject
import org.mind.app.domain.model.promotion.Promotions
import org.mind.app.domain.model.subcategories.SubCategoriesItem
import org.mind.app.domain.model.subquestions.SubQuestionsItem
import org.mind.app.domain.model.users.Users
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.LoadingBox
import org.mind.app.presentation.ui.screens.chat.ChatDetailScreen
import org.mind.app.presentation.ui.screens.chat.ChatScreen
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark

object ChatDetailTab : Tab {
    @Composable
    override fun Content() {
        LocalPreferenceProvider {
            val viewModel: MainViewModel = koinInject()
            val preference = LocalPreference.current
            val navigator = LocalNavigator.current
            val isDark by LocalThemeIsDark.current
            var email by remember { mutableStateOf("") }
            var isLogin by remember { mutableStateOf(false) }
            var allUsersList by remember { mutableStateOf(emptyList<Users>()) }
            LaunchedEffect(Unit) {
                email = preference.getString("email").toString()
                isLogin = preference.getBoolean("is_login", false)
            }
            LaunchedEffect(Unit) {
                viewModel.getAllUsers()
            }
            val allUsers by viewModel.allUsers.collectAsState()

            when (allUsers) {
                is ResultState.Error -> {
                    val error = (allUsers as ResultState.Error).message
                    ErrorBox(error)
                }

                ResultState.Loading -> {
                    LoadingBox()
                }

                is ResultState.Success -> {
                    val response = (allUsers as ResultState.Success).data
                    allUsersList = response
                }
            }
            Navigator(ChatScreen(allUsersList))
        }

    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.ChatBubble)
            val title = "Chat Detail"
            val index: UShort = 3u
            return TabOptions(index, title, icon)
        }
}