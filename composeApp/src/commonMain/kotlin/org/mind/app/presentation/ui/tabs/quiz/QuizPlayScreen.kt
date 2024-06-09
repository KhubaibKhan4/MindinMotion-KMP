package org.mind.app.presentation.ui.tabs.quiz

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.mind.app.domain.model.category.QuizCategoryItem
import org.mind.app.domain.model.quiz.QuizQuestionsItem
import org.mind.app.presentation.ui.screens.quiz.QuizQuestionsScreen
import org.mind.app.presentation.ui.screens.quiz.QuizScreenPlay

class QuizPlayScreen(
    private val quizCategoryItem: QuizCategoryItem,
    private val quizQuestionsItem: List<QuizQuestionsItem>
) : Screen {
    @Composable
    override fun Content() {
        Navigator(QuizScreenPlay(quizCategoryItem,quizQuestionsItem))
    }
}