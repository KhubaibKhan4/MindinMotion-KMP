package org.mind.app.presentation.ui.screens.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.mind.app.domain.model.category.QuizCategoryItem

class QuizQuestionsScreen(
    private val quizCategoryItem: QuizCategoryItem
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
       Scaffold(
           topBar = {
               TopAppBar(
                   title = { Text(quizCategoryItem.name) }
               )
           }
       ) {
           Column(
               modifier = Modifier.fillMaxWidth()
                   .padding(top = it.calculateTopPadding())
                   .padding(start = 16.dp, end = 16.dp)
           ) {
               Text(
                   text = quizCategoryItem.name + quizCategoryItem.description
               )
           }
       }
    }
}
