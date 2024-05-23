package org.mind.app.presentation.ui.tabs.Instructors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object InstructorsTab : Tab {
    @Composable
    override fun Content() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Instructors Tab")
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title by remember { mutableStateOf("Instructors") }
            val icon = rememberVectorPainter(Icons.Default.IntegrationInstructions)
            val index: UShort = 1u
            return TabOptions(index, title, icon)
        }
}