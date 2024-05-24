package org.mind.app.presentation.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import org.koin.compose.koinInject
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.screens.auth.login.LoginScreen
import org.mind.app.presentation.viewmodel.MainViewModel

class ProfileScreen() : Screen {
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
        val navigator = LocalNavigator.current
        var isMenuVisible by remember { mutableStateOf(false) }
        val signOutState by viewModel.signOutState.collectAsState()

        when (signOutState) {
            is ResultState.Loading -> {

            }

            is ResultState.Success -> {
                LaunchedEffect(Unit) {
                    preference.put("is_login", false)
                    preference.put("email", "")
                    navigator?.let {
                        while (it.canPop) {
                            it.pop()
                        }
                        it.push(LoginScreen())
                    }
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
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                isMenuVisible = !isMenuVisible
                            }
                        )
                        if (isMenuVisible) {
                            DropdownMenu(
                                expanded = isMenuVisible,
                                onDismissRequest = { isMenuVisible = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Logout") },
                                    onClick = {
                                        viewModel.signOut()
                                    }
                                )
                            }
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = it.calculateTopPadding()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

            }
        }
    }
}