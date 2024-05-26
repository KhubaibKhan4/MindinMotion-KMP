package org.mind.app.presentation.ui.screens.setting

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.SwitchDefaults
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
import org.mind.app.notify
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.LoadingBox
import org.mind.app.presentation.ui.screens.auth.login.LoginScreen
import org.mind.app.presentation.ui.screens.setting.about.AboutScreen
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
        var isLoading by remember { mutableStateOf(false) }
        val signOutState by viewModel.signOutState.collectAsState()

        LaunchedEffect(isLogin) {
            isLogin = preference.getBoolean("is_login", false)
        }
        LaunchedEffect(isDark) {
            preference.put("is_dark", isDark)
        }
        when (signOutState) {
            is ResultState.Loading -> {
                if (isLoading) {
                    LoadingBox()
                }
            }

            is ResultState.Success -> {
                val response = (signOutState as ResultState.Success).data
                notify(response)
                LaunchedEffect(Unit) {
                    preference.put("is_login", false)
                    tabNavigator.current = LoginScreen
                    isLoading = false
                }
            }

            is ResultState.Error -> {
                val error = (signOutState as ResultState.Error).message
                ErrorBox(error)
                isLoading = false
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
        ) { padding ->
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
                        AnimatedSwitch(
                            isDark = isDark,
                            onCheckedChange = {
                                isDark = it
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
                        navigator?.push(AboutScreen())
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
                        isLogin = true
                        viewModel.signOut()
                    }
                )
            }
        }
    }
}
@Composable
fun AnimatedSwitch(
    isDark: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val thumbColor by animateColorAsState(
        targetValue = if (isDark) Color.DarkGray else Color.White
    )
    val trackColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF1C1C1C) else Color(0xFFB0BEC5)
    )

    Switch(
        modifier = Modifier.size(30.dp),
        checked = isDark,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = thumbColor,
            uncheckedThumbColor = thumbColor,
            checkedTrackColor = trackColor,
            uncheckedTrackColor = trackColor
        ),
        thumbContent = {
            Icon(
                imageVector = if (isDark) Icons.Default.NightsStay else Icons.Default.WbSunny,
                contentDescription = null,
            )
        }
    )
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
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
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