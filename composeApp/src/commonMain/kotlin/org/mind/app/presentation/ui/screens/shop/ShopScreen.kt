package org.mind.app.presentation.ui.screens.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider

class ShopScreen : Screen {
    @Composable
    override fun Content() {
        ShopContent()
    }
}

@Composable
fun ShopContent() {
    LocalPreferenceProvider {
        val preference = LocalPreference.current
        var email by remember { mutableStateOf("") }
        var isLogin by remember { mutableStateOf(false) }
        LaunchedEffect(Unit){
            isLogin = preference.getBoolean("is_login",false)
            email = preference.getString("email").toString()
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Shop Content Email: $email & isLogin: $isLogin")
        }
    }
}