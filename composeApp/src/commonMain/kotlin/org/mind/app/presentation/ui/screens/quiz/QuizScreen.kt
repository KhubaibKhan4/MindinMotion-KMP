package org.mind.app.presentation.ui.screens.quiz

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.koinInject
import org.mind.app.domain.model.category.QuizCategoryItem
import org.mind.app.domain.model.quiz.QuizQuestionsItem
import org.mind.app.domain.usecases.ResultState
import org.mind.app.notify
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.LoadingBox
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
    val isDark by LocalThemeIsDark.current
    var quizQuestions by remember { mutableStateOf<List<QuizQuestionsItem>?>(null) }
    var categories by remember { mutableStateOf<List<QuizCategoryItem>?>(null) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

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

    val filteredCategories = categories?.filter { category ->
        category.name.contains(searchQuery, ignoreCase = true)
    }

    val quizItemsWithCategories = quizQuestions?.mapNotNull { quiz ->
        val category = filteredCategories?.find { it.id == quiz.categoryId }
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
                    if (isSearchActive) {
                        androidx.compose.material3.TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                            ,
                            placeholder = {
                                Text("Search Notes", color = Color.Black)
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    modifier = Modifier.clickable {
                                        searchQuery = ""
                                        isSearchActive = !isSearchActive
                                    }
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = androidx.compose.material3.TextFieldDefaults.colors(
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                            )
                        )
                    } else {
                        Text(
                            "Quiz",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    }
                }
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
            if (filteredCategories.isNullOrEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No items found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredCategories.forEach { category ->
                        val quizItemsForCategory = quizItemsWithCategories
                            ?.filter { it.second.id == category.id }
                            ?.map { it.first }

                        item {
                            if (quizItemsForCategory?.isNotEmpty() == true && quizItemsForCategory.isNotEmpty()) {
                                QuizCategoryItemCard(quizItemsForCategory, category)
                            }
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
    val localNavigator = LocalNavigator.current
    val isDark by LocalThemeIsDark.current
    var isEmpty by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                if (quizItems.isEmpty()) {
                    isEmpty = true
                } else {
                    localNavigator?.push(QuizScreenPlay(category, quizItems))
                }
            }
    ) {
        if (isEmpty) {
            notify("No Questions Found.")
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom
            ) {
                Spacer(modifier = Modifier.height(64.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "${quizItems.size} Questions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
        val image: Resource<Painter> = asyncPainterResource(BASE_URL + category.imageUrl)
        KamelImage(
            resource = image,
            contentDescription = null,
            modifier = Modifier
                .size(128.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-6).dp)
                .aspectRatio(16f / 13f)
                .clip(RoundedCornerShape(12.dp))
                .shadow(elevation = 8.dp),
            contentScale = ContentScale.FillBounds,
            animationSpec = tween(
                durationMillis = 500,
                delayMillis = 100,
                easing = androidx.compose.animation.core.LinearEasing
            )
        )
    }
}