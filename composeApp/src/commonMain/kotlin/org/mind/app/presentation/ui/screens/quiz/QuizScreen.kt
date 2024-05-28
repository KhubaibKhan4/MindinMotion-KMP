package org.mind.app.presentation.ui.screens.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.koinInject
import org.mind.app.domain.model.category.QuizCategoryItem
import org.mind.app.domain.model.quiz.QuizQuestionsItem
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.LoadingBox
import org.mind.app.presentation.ui.tabs.quiz.QuizQuestions
import org.mind.app.presentation.ui.tabs.quiz.QuizTab
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark
import org.mind.app.utils.Constant.BASE_URL

class QuizScreen : Screen {
    @Composable
    override fun Content() {
        QuizScreenContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreenContent(
    viewModel: MainViewModel = koinInject(),
) {
    var quizQuestions by remember { mutableStateOf<List<QuizQuestionsItem>?>(null) }
    var categories by remember { mutableStateOf<List<QuizCategoryItem>?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllQuizQuestions()
        viewModel.getAllCategories()
    }

    val quizState by viewModel.quizQuestions.collectAsState()
    when (quizState) {
        is ResultState.Error -> {
            val error = (quizState as ResultState.Error).message
            ErrorBox(error)
        }

        ResultState.Loading -> {
            LoadingBox()
        }

        is ResultState.Success -> {
            val response = (quizState as ResultState.Success).data
            quizQuestions = response
        }
    }

    val categoryState by viewModel.quizCategories.collectAsState()
    when (categoryState) {
        is ResultState.Error -> {
            val error = (categoryState as ResultState.Error).message
            ErrorBox(error)
        }

        ResultState.Loading -> {
            LoadingBox()
        }

        is ResultState.Success -> {
            val response = (categoryState as ResultState.Success).data
            categories = response
        }
    }

    val quizItemsWithCategories = quizQuestions?.mapNotNull { quiz ->
        val category = categories?.find { it.id == quiz.categoryId }
        if (category != null) {
            Pair(quiz, category)
        } else {
            null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Quiz")
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .padding(top = it.calculateTopPadding(), start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                categories?.forEach { category ->
                    val quizItemsForCategory = quizItemsWithCategories
                        ?.filter { it.second.id == category.id }
                        ?.map { it.first }

                    item {
                        if (quizItemsForCategory != null && quizItemsForCategory.isNotEmpty()) {
                            QuizCategoryItemCard(quizItemsForCategory, category)
                        } else {
                            Text("No Items Found for ${category.name}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizCategoryItemCard(
    quizItems: List<QuizQuestionsItem>,
    category: QuizCategoryItem,
) {
    val navigator = LocalTabNavigator.current
    val isDark by LocalThemeIsDark.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {
                navigator.current = QuizQuestions(category, quizItems)
            },
        border = BorderStroke(width = if(isDark) 1.dp else 2.dp, color = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val image: Resource<Painter> = asyncPainterResource(BASE_URL + category.imageUrl)
                KamelImage(
                    resource = image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}