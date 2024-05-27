package org.mind.app.presentation.ui.tabs.Instructors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider

object InstructorsTab : Tab {
    @Composable
    override fun Content() {
        LocalPreferenceProvider {
            val preference = LocalPreference.current
            var email by remember { mutableStateOf("") }
            var isLogin by remember { mutableStateOf(false) }
            LaunchedEffect(Unit){
                email = preference.getString("email").toString()
                isLogin = preference.getBoolean("is_login",false)

            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Instructors Tab $email & $isLogin")
            }
        }

    }

    override val options: TabOptions
        @Composable
        get() {
            val title by remember { mutableStateOf("Instructors") }
            val icon = rememberVectorPainter(Icons.Default.IntegrationInstructions)
            val index: UShort = 2u
            return TabOptions(index, title, icon)
        }
}