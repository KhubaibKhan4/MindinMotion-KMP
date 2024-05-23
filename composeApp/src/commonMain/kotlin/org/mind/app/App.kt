package org.mind.app

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.mind.app.presentation.ui.screens.auth.login.LoginScreen
import org.mind.app.theme.AppTheme

@Composable
internal fun App() = AppTheme {
    Navigator(LoginScreen())
}

internal expect fun openUrl(url: String?)