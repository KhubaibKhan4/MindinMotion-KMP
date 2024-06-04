package org.mind.app.presentation.ui.tabs.resume

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.mind.app.presentation.ui.screens.resume.ResumeScreen

object ResumeTab : Tab {
    @Composable
    override fun Content() {
        Navigator(ResumeScreen())
    }

    override val options: TabOptions
        @Composable
        get() {
            val title by remember { mutableStateOf("Resume") }
            val icon = rememberVectorPainter(Icons.Default.PictureAsPdf)
            val index: UShort = 3u
            return TabOptions(index, title, icon)
        }
}