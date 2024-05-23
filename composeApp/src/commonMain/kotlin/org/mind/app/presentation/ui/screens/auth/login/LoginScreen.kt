package org.mind.app.presentation.ui.screens.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mind.app.domain.repository.Repository
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.screens.auth.signup.SignupScreen
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark
import org.mind.app.utils.isValidEmail
import org.mind.app.utils.isValidPassword

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        LoginContent()
    }
}

@Composable
fun LoginContent() {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val viewModel = remember { MainViewModel(Repository(Firebase.auth)) }
    var isDark by LocalThemeIsDark.current
    var userMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.current

    val state by viewModel.loginUser.collectAsState()
    when (state) {
        is ResultState.Error -> {
            val error = (state as ResultState.Error).message
            userMessage = error
            isLoading = false
        }

        is ResultState.Loading -> {
        }

        is ResultState.Success -> {
            val response = (state as ResultState.Success).data
            userMessage = response
            isLoading = false
            if (userMessage.contains("Success")) {
                email = ""
                pass = ""
                navigator?.pop()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, "")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        Button(
            onClick = {
                when {
                    !isValidEmail(email) -> {
                        userMessage = "Invalid email format"
                    }

                    !isValidPassword(pass) -> {
                        userMessage =
                            "Password must be at least 8 characters long and contain an uppercase letter"
                    }

                    else -> {
                        viewModel.login(email, pass)
                        isLoading = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Login", fontSize = 16.sp, color = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                AnimatedVisibility(
                    visible = isLoading,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(25.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        TextButton(
            onClick = {
                navigator?.push(SignupScreen())
            }
        ) {
            Text("Don't have an account? Sign up")
        }

        if (userMessage.isNotEmpty()) {
            LaunchedEffect(userMessage) {
                scope.launch {
                    delay(2000)
                    userMessage = ""
                }
            }
            Text(
                userMessage,
                color = if (state is ResultState.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
    }
}