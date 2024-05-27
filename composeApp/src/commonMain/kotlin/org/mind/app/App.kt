package org.mind.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.cash.sqldelight.db.SqlDriver
import cafe.adriel.voyager.navigator.Navigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import org.mind.app.presentation.ui.screens.auth.login.LoginScreen
import org.mind.app.presentation.ui.screens.main.MainScreen
import org.mind.app.theme.AppTheme
import org.mind.app.theme.LocalThemeIsDark

@Composable
internal fun App() = AppTheme {
    LocalPreferenceProvider {
        val preference = LocalPreference.current
        val isDarkMode by remember { mutableStateOf(preference.getBoolean("is_dark", false)) }
        var isDark by LocalThemeIsDark.current
        LaunchedEffect(isDarkMode) {
            isDark = isDarkMode
        }
        val isLoggedIn by remember { mutableStateOf(preference.getBoolean("is_login", false)) }
        if (isLoggedIn) {
            Navigator(MainScreen)
        } else {
            Navigator(LoginScreen)
        }
    }
}


internal expect fun openUrl(url: String?)

@Composable
internal expect fun notify(message: String)
expect fun createDriver(): SqlDriver