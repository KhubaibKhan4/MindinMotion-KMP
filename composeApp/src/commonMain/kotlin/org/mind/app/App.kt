package org.mind.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import mind_in_motion.composeapp.generated.resources.IndieFlower_Regular
import mind_in_motion.composeapp.generated.resources.Res
import mind_in_motion.composeapp.generated.resources.cyclone
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import org.mind.app.domain.repository.Repository
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.AppTheme
import org.mind.app.theme.LocalThemeIsDark

@Composable
internal fun App() = AppTheme {
    val viewModel = remember { MainViewModel(Repository()) }
    var isDark by LocalThemeIsDark.current
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var userMessage by remember { mutableStateOf("") }
    val state by viewModel.createUser.collectAsState()
    when (state) {
        is ResultState.Error -> {
            val error = (state as ResultState.Error).message
            userMessage = error
        }

        is ResultState.Loading -> {
            CircularProgressIndicator()
        }

        is ResultState.Success -> {
            val response = (state as ResultState.Success).data
            userMessage = response
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.cyclone),
            fontFamily = FontFamily(Font(Res.font.IndieFlower_Regular)),
            style = MaterialTheme.typography.displayLarge
        )
        TextField(
            value = email,
            onValueChange = {
                email = it
            },
            placeholder = {
                Text("Email")
            }
        )
        TextField(
            value = pass,
            onValueChange = {
                pass = it
            },
            placeholder = {
                Text("Password")
            }
        )
        Button(
            onClick = {
                viewModel.createUser(email, pass)
            },
        ) {
            Text("Create Account")
        }
        Text(text = userMessage)
    }
}

internal expect fun openUrl(url: String?)