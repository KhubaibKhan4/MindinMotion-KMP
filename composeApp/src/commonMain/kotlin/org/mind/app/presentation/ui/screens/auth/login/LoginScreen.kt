package org.mind.app.presentation.ui.screens.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.benasher44.uuid.Uuid
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mind_in_motion.composeapp.generated.resources.Res
import mind_in_motion.composeapp.generated.resources.ic_logo
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.mind.app.domain.usecases.ResultState
import org.mind.app.notify
import org.mind.app.presentation.ui.screens.auth.reset.ResetPasswordScreen
import org.mind.app.presentation.ui.screens.auth.signup.SignupScreen
import org.mind.app.presentation.ui.screens.main.MainScreen
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark
import org.mind.app.utils.isValidEmail
import org.mind.app.utils.isValidPassword
import kotlin.time.Duration.Companion.seconds

object LoginScreen : Tab {
    @Composable
    override fun Content() {
        LoginContent()
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Visibility)
            val title = "Login"
            val index: UShort = 5u
            return TabOptions(index, title, icon)
        }
}

@Composable
fun LoginContent(
    viewModel: MainViewModel = koinInject(),
) {
    LocalPreferenceProvider {
        val preference = LocalPreference.current
        var value by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var pass by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var isDark by LocalThemeIsDark.current
        var userMessage by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        val navigator = LocalTabNavigator.current


        val state by viewModel.loginUser.collectAsState()
        when (state) {
            is ResultState.Error -> {
                val error = (state as ResultState.Error).message
                userMessage = error
                notify(userMessage)
                scope.launch {
                    delay(2.seconds)
                    userMessage = ""
                }
                isLoading = false
            }

            is ResultState.Loading -> {

            }

            is ResultState.Success -> {
                val response = (state as ResultState.Success).data
                userMessage = response
                notify(userMessage)
                isLoading = false
                if (userMessage.contains("Success")) {
                    scope.launch {
                        email = ""
                        pass = ""
                        preference.put("is_login", true)
                        navigator.current = MainScreen(Uuid(212L,8L).mostSignificantBits.toString())
                        delay(2.seconds)
                    }
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
            Image(
                painter = painterResource(Res.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = if (isDark) Color.White else Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    )
            )
            Spacer(modifier = Modifier.height(20.dp))
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
                    .padding(bottom = 6.dp)
            )
            TextButton(
                onClick = {
                    navigator.current = ResetPasswordScreen
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?")
            }

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
                            preference.put("email", email)
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(
                    onClick = {
                        navigator.current = SignupScreen
                    }
                ) {
                    Text("Don't have an account? Sign up")
                }

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

}