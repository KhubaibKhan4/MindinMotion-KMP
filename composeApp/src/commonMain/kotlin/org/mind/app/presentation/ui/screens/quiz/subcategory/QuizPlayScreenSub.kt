package org.mind.app.presentation.ui.screens.quiz.subcategory

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
import org.mind.app.domain.model.quiz.QuizQuestionsItem
import org.mind.app.domain.model.subcategories.SubCategoriesItem
import org.mind.app.domain.model.subquestions.SubQuestionsItem
import org.mind.app.presentation.ui.screens.quiz.QuizScreenPlay

class QuizPlayScreenSub(
    private val quizCategoryItem: SubCategoriesItem,
    private val quizQuestionsItem: List<SubQuestionsItem>
) : Tab {
    @Composable
    override fun Content() {
        Navigator(QuizScreenPlaySubScreen(quizCategoryItem,quizQuestionsItem))
    }

    override val options: TabOptions
        @Composable
        get() {
            val title by remember { mutableStateOf("QuizPlayScreen") }
            val icon = rememberVectorPainter(Icons.Default.Quiz)
            val index: UShort = 113u
            return TabOptions(index, title, icon)
        }
}