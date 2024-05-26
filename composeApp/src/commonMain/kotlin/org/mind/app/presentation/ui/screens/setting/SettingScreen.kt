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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.cmppreference.LocalPreference
import org.koin.compose.koinInject
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.screens.auth.login.LoginScreen
import org.mind.app.presentation.ui.tabs.profile.ProfileTab
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark

class SettingScreen : Screen {
    @Composable
    override fun Content() {
        SettingScreenContent()
    }

   /* override val options: TabOptions
        @Composable
        get() {
            val index : UShort = 8u
            val title = "Setting"
            val icon = rememberVectorPainter(Icons.Default.Settings)
            return TabOptions(index, title, icon)
        }*/
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreenContent(
    viewModel: MainViewModel = koinInject()
) {
    val tabNavigator = LocalTabNavigator.current
    val navigator = LocalNavigator.current
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
                tabNavigator.current = LoginScreen
            }
        }

        is ResultState.Error -> {
           // val errorMessage = (signOutState as ResultState.Error).message
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
                icon = Icons.AutoMirrored.Filled.Logout,
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
