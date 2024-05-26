package org.mind.app.presentation.ui.screens.setting

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.cmppreference.LocalPreference
import org.koin.compose.koinInject
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.screens.auth.login.LoginScreen
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark

class SettingScreen : Screen {
    @Composable
    override fun Content() {
        SettingScreenContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreenContent(
    viewModel: MainViewModel = koinInject()
) {
    val navigator = LocalTabNavigator.current
    val preference = LocalPreference.current
    var isDark by LocalThemeIsDark.current
    var isLogin by remember { mutableStateOf(false) }
    val signOutState by viewModel.signOutState.collectAsState()
    LaunchedEffect(isLogin) {
        isLogin = preference.getBoolean("is_login", false)
    }

    when (signOutState) {
        is ResultState.Loading -> {
            // Handle loading state
        }

        is ResultState.Success -> {
            LaunchedEffect(Unit) {
                preference.put("is_login", false)
                navigator.current = LoginScreen
            }
        }

        is ResultState.Error -> {
            val errorMessage = (signOutState as ResultState.Error).message
            // Handle error state
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            SettingItem(
                icon = Icons.Default.WbSunny,
                title = "Dark Mode",
                onClick = { isDark = !isDark }
            )
            SettingItem(
                icon = Icons.Default.Edit,
                title = "Edit Profile",
                onClick = {
                    // Navigate to edit profile screen
                }
            )
            SettingItem(
                icon = Icons.Default.Info,
                title = "About App",
                onClick = {
                    // Navigate to about app screen
                }
            )
            SettingItem(
                icon = Icons.Default.Policy,
                title = "Privacy Policy",
                onClick = {
                    // Navigate to privacy policy screen
                }
            )
            SettingItem(
                icon = Icons.Default.Logout,
                title = "Logout",
                onClick = {
                    viewModel.signOut()
                }
            )
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
