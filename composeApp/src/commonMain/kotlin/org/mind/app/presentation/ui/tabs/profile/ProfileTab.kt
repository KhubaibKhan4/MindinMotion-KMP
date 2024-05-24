package org.mind.app.presentation.ui.tabs.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.mind.app.presentation.ui.screens.home.HomeScreen

object ProfileTab: Tab {
    @Composable
    override fun Content() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Profile Tab")
        }
    }

    override val options: TabOptions
        @Composable
        get()  {
            val title by remember { mutableStateOf("Person") }
            val icon = rememberVectorPainter(Icons.Default.Person)
            val index: UShort = 3u
            return TabOptions(index, title, icon)
        }
}