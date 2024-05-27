package org.mind.app.presentation.ui.screens.quiz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen

class QuizScreen : Screen {
    @Composable
    override fun Content() {
        QuizScreenContent()
    }
}

@Composable
fun QuizScreenContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Quiz Screen")
    }
}