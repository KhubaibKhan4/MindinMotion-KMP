package org.mind.app.presentation.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        LocalPreferenceProvider {
            val preference = LocalPreference.current
            var email by remember { mutableStateOf("") }
            var is_Login by remember { mutableStateOf(false) }
            email = preference.getString("email").toString()
            is_Login = preference.getBoolean("is_login",false)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("HomeTab $email & $is_Login")
            }
        }
    }
}