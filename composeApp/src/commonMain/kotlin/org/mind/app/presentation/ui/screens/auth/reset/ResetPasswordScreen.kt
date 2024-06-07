package org.mind.app.presentation.ui.screens.auth.reset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.koin.compose.koinInject
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.screens.auth.login.LoginScreen
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.utils.isValidEmail
import kotlin.random.Random

object ResetPasswordScreen : Tab{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        var email by remember { mutableStateOf("") }
        var userMessage by remember { mutableStateOf("") }
        val viewModel: MainViewModel = koinInject()
        val navigator = LocalTabNavigator.current
        val resetPasswordState by viewModel.resetPasswordState.collectAsState()
        LaunchedEffect(resetPasswordState) {
            when (resetPasswordState) {
                is ResultState.Success -> {
                    if (email.isNotEmpty()) {
                        userMessage = (resetPasswordState as ResultState.Success<String>).data
                    }
                }
                is ResultState.Error -> {
                    if (email.isNotEmpty()) {
                        userMessage = (resetPasswordState as ResultState.Error).message
                        userMessage = ""
                    }
                }
                is ResultState.Loading -> {
                    if (email.isNotEmpty()) {
                        userMessage = "Sending reset email..."
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.current = LoginScreen }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBackIosNew,
                                contentDescription = "Back"
                            )
                        }
                    }
                )

            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Button(
                    onClick = {
                        if (!isValidEmail(email)) {
                            userMessage = "Invalid email format"
                        } else {
                            viewModel.resetPassword(email)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Reset Password", fontSize = 16.sp, color = Color.White)
                }

                if (userMessage.isNotEmpty()) {
                    Text(
                        userMessage,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.LockReset)
            val title = "Reset"
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