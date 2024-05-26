package org.mind.app.presentation.ui.screens.setting

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import org.koin.compose.koinInject
import org.mind.app.domain.model.users.Users
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.screens.auth.login.LoginScreen
import org.mind.app.presentation.ui.screens.setting.edit.EditProfileScreen
import org.mind.app.presentation.ui.tabs.profile.ProfileTab
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark

class SettingScreen(private val users: Users) : Screen {
    @Composable
    override fun Content() {
        SettingScreenContent(users)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreenContent(
    users: Users,
    viewModel: MainViewModel = koinInject(),
) {
    LocalPreferenceProvider {
        val tabNavigator = LocalTabNavigator.current
        val navigator = LocalNavigator.current
        val preference = LocalPreference.current
        var isDark by LocalThemeIsDark.current
        var isLogin by remember { mutableStateOf(false) }
        val signOutState by viewModel.signOutState.collectAsState()

        LaunchedEffect(isLogin) {
            isLogin = preference.getBoolean("is_login", false)
        }
        LaunchedEffect(isDark) {
            preference.put("is_dark", isDark)
        }
        when (signOutState) {
            is ResultState.Loading -> {
                // Handle loading state
            }

            is ResultState.Success -> {
                LaunchedEffect(Unit) {
                    preference.put("is_login", false)
                    tabNavigator.current = LoginScreen
                }
            }

            is ResultState.Error -> {
                // Handle error state
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                navigator?.push(ProfileTab)
                            }
                        )
                    }
                )
            }
        ) {padding->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                SettingCard(
                    title = "Dark Mode",
                    icon = if (isDark) Icons.Default.NightsStay else Icons.Default.WbSunny,
                    content = {
                        Switch(
                            checked = isDark,
                            onCheckedChange = {
                                isDark = it
                            },
                            thumbContent = {
                                Icon(
                                    imageVector = if (isDark) Icons.Default.NightsStay else Icons.Default.WbSunny,
                                    contentDescription = null,
                                )
                            }
                        )
                    }
                )

                SettingCard(
                    title = "Edit Profile",
                    icon = Icons.Default.Edit,
                    onClick = {
                        navigator?.push(EditProfileScreen(users))
                    }
                )

                SettingCard(
                    title = "About App",
                    icon = Icons.Default.Info,
                    onClick = {
                        // Navigate to about app screen
                    }
                )

                SettingCard(
                    title = "Privacy Policy",
                    icon = Icons.Default.Policy,
                    onClick = {
                        // Navigate to privacy policy screen
                    }
                )

                SettingCard(
                    title = "Logout",
                    icon = Icons.AutoMirrored.Filled.Logout,
                    onClick = {
                        viewModel.signOut()
                    }
                )
            }
        }
    }
}

@Composable
fun SettingCard(
    title: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick ?: {}),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            if (onClick == null) {
                content()
            }
        }
    }
}