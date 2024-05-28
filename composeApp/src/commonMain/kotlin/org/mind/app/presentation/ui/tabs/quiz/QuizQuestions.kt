package org.mind.app.presentation.ui.tabs.quiz

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.mind.app.domain.model.category.QuizCategoryItem
import org.mind.app.presentation.ui.screens.quiz.QuizQuestionsScreen

class QuizQuestions(
    private val quizCategoryItem: QuizCategoryItem
) : Tab {
    @Composable
    override fun Content() {
        Navigator(QuizQuestionsScreen(quizCategoryItem))
    }

    override val options: TabOptions
        @Composable
        get() {
            val title by remember { mutableStateOf("QuizQuestions") }
            val icon = rememberVectorPainter(Icons.Default.Quiz)
            val index: UShort = 112u
            return TabOptions(index, title, icon)
        }
}