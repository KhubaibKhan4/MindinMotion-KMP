package org.mind.app.presentation.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import org.koin.compose.koinInject
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.components.LocalImage
import org.mind.app.presentation.ui.screens.auth.login.LoginScreen
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark

class ProfileScreen : Screen {
    @Composable
    override fun Content() {
        ProfileScreenContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    viewModel: MainViewModel = koinInject(),
) {
    LocalPreferenceProvider {
        val preference = LocalPreference.current
        val navigator = LocalTabNavigator.current
        var isDark by LocalThemeIsDark.current
        var isMenuVisible by remember { mutableStateOf(false) }
        var isLogin by remember { mutableStateOf(false) }
        var email by remember { mutableStateOf("") }
        val signOutState by viewModel.signOutState.collectAsState()
        LaunchedEffect(isLogin) {
            isLogin = preference.getBoolean("is_login", false)
            email = preference.getString("email").toString()
        }
        when (signOutState) {
            is ResultState.Loading -> {

            }

            is ResultState.Success -> {
                LaunchedEffect(Unit) {
                    preference.put("is_login", false)
                    preference.put("email", "")
                    isLogin = preference.getBoolean("is_login", false)
                    email = preference.getString("email").toString()
                    navigator.current = LoginScreen
                }
            }

            is ResultState.Error -> {
                val errorMessage = (signOutState as ResultState.Error).message
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Profile") },
                    actions = {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                isDark = !isDark
                            }
                        )
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(top = it.calculateTopPadding()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
               // Text("Shop Content Email: $email & isLogin: $isLogin")
                LocalImage(modifier = Modifier.size(150.dp).clip(CircleShape))
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Lidya Nada",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = if (isDark) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "lidayanada@gmail.com",
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}