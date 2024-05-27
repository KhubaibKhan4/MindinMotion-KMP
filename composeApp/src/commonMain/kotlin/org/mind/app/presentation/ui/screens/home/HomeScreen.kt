package org.mind.app.presentation.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import org.mind.app.presentation.ui.tabs.chat.ChatTab

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        LocalPreferenceProvider {
            val preference = LocalPreference.current
            val tabNavigator = LocalTabNavigator.current
            var email by remember { mutableStateOf("") }
            var isLogin by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                email = preference.getString("email").toString()
                isLogin = preference.getBoolean("is_login", false)
            }
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("") },
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            tabNavigator.current = ChatTab
                        }
                    ){
                        Icon(
                            imageVector = Icons.Outlined.ChatBubble,
                            contentDescription = null
                        )
                    }
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("HomeTab $email & $isLogin")
                }
            }
        }
    }
}