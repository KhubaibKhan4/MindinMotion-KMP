package org.mind.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import app.cash.sqldelight.db.SqlDriver
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.benasher44.uuid.UUID
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import dev.gitlive.firebase.storage.File
import io.ktor.client.statement.HttpResponse
import org.mind.app.presentation.ui.screens.auth.login.LoginScreen
import org.mind.app.presentation.ui.screens.main.MainScreen
import org.mind.app.theme.AppTheme
import org.mind.app.theme.LocalThemeIsDark

@Composable
internal fun App() = AppTheme {
    LocalPreferenceProvider {
        val preference = LocalPreference.current
        var isDark by LocalThemeIsDark.current
        val isDarkMode by remember { mutableStateOf(preference.getBoolean("is_dark", false)) }
        LaunchedEffect(isDarkMode) {
            isDark = isDarkMode
        }
        val isLoggedIn by remember { mutableStateOf(preference.getBoolean("is_login", false)) }
        TabNavigator(
            tab = if (isLoggedIn) MainScreen(Uuid(534L,4L).mostSignificantBits.toString()) else LoginScreen,
            key = if (isLoggedIn) "MainScreenKey" else "LoginScreenKey"
        )
    }
}


internal expect fun openUrl(url: String?)

@Composable
internal expect fun notify(message: String)
expect fun createDriver(): SqlDriver

@Composable
expect fun providePDF(url: String)
expect fun createTempFileFromBitmap(imageBitmap: ImageBitmap): File
expect fun createTempFile(name: String, extension: String): String
expect suspend fun saveResponseToFile(response: HttpResponse, filePath: String)
expect fun sharePdf(pdfFilePath: String)