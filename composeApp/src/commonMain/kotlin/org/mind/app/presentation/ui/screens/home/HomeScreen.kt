package org.mind.app.presentation.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import mind_in_motion.composeapp.generated.resources.Res
import mind_in_motion.composeapp.generated.resources.avatar
import mind_in_motion.composeapp.generated.resources.quiz_banner
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.mind.app.domain.model.subcategories.SubCategoriesItem
import org.mind.app.domain.model.subquestions.SubQuestionsItem
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.LoadingBox
import org.mind.app.presentation.ui.screens.quiz.QuizQuestionsScreen
import org.mind.app.presentation.ui.screens.quiz.subcategory.QuizPlayScreenSub
import org.mind.app.presentation.ui.screens.quiz.subcategory.QuizQuestionsSub
import org.mind.app.presentation.ui.screens.quiz.subcategory.QuizScreenPlaySubScreen
import org.mind.app.presentation.ui.tabs.chat.ChatTab
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.utils.Constant.BASE_URL

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        LocalPreferenceProvider {
            val viewModel: MainViewModel = koinInject()
            val preference = LocalPreference.current
            val tabNavigator = LocalTabNavigator.current
            var email by remember { mutableStateOf("") }
            var isLogin by remember { mutableStateOf(false) }
            var subCategoriesItems by remember { mutableStateOf(emptyList<SubCategoriesItem>()) }
            var subQuestionsItems by remember { mutableStateOf(emptyList<SubQuestionsItem>()) }
            LaunchedEffect(Unit) {
                email = preference.getString("email").toString()
                isLogin = preference.getBoolean("is_login", false)
            }
            LaunchedEffect(Unit) {
                viewModel.getAllSubCategories()
                viewModel.getAllSubQuestions()
            }
            val subCategories by viewModel.subCategories.collectAsState()
            val subQuestions by viewModel.subQuestions.collectAsState()
            when (subCategories) {
                is ResultState.Error -> {
                    val error = (subCategories as ResultState.Error).message
                    ErrorBox(error)
                }

                ResultState.Loading -> {
                    LoadingBox()
                }

                is ResultState.Success -> {
                    val response = (subCategories as ResultState.Success).data
                    subCategoriesItems = response
                }
            }
            when (subQuestions) {
                is ResultState.Error -> {
                    val error = (subQuestions as ResultState.Error).message
                    ErrorBox(error)
                }

                ResultState.Loading -> {
                    LoadingBox()
                }

                is ResultState.Success -> {
                    val response = (subQuestions as ResultState.Success).data
                    subQuestionsItems = response
                }
            }
            val discoverSubCategories = subCategoriesItems.filter { it.categoryName == "Discover" }
            val topCollectionsSubCategories =
                subCategoriesItems.filter { it.categoryName == "Top Collections" }
            val trendingQuizSubCategories =
                subCategoriesItems.filter { it.categoryName == "Trending Quiz" }
            val topPicksSubCategories = subCategoriesItems.filter { it.categoryName == "Top Picks" }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Mind in Motion") },
                        actions = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null
                            )
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            tabNavigator.current = ChatTab
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubble,
                            contentDescription = null
                        )
                    }
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = it.calculateTopPadding())
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Image(
                        painter = painterResource(Res.drawable.quiz_banner),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(start = 12.dp, end = 12.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                    SubCategoryItem("Discover", discoverSubCategories, subQuestionsItems)
                    SubCategoryItem(
                        "Top Collections",
                        topCollectionsSubCategories,
                        subQuestionsItems
                    )
                    SubCategoryItem("Trending Quiz", trendingQuizSubCategories, subQuestionsItems)
                    SubCategoryItem("Top Picks", topPicksSubCategories, subQuestionsItems)
                }
            }
        }
    }
}

@Composable
fun SubCategoryItem(
    categoryName: String,
    subCategoryItems: List<SubCategoriesItem>,
    subQuestionsItems: List<SubQuestionsItem>,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryName,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            Text(
                text = "See All",
                modifier = Modifier.padding(end = 16.dp),
                style = TextStyle(
                    color = Color.Blue,
                    fontSize = 16.sp
                )
            )
        }
        if (categoryName == "Top Collections") {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(subCategoryItems) { subCategoryItem ->
                    TopCollectionCard(subCategoryItem, subQuestionsItems)
                }
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(subCategoryItems) { subCategoryItem ->
                    SubCategoryCard(subCategoryItem, subQuestionsItems)
                }
            }
        }
    }
}

@Composable
fun SubCategoryCard(
    subCategoryItem: SubCategoriesItem,
    subQuestionsItems: List<SubQuestionsItem>,
) {
    val navigator = LocalNavigator.current
    val questionsForCategory = subQuestionsItems.filter { it.categoryId == subCategoryItem.id }
    val questionCount = questionsForCategory.size
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.width(200.dp)
            .clickable {
                navigator?.push(QuizScreenPlaySubScreen(subCategoryItem, questionsForCategory))
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                val image: Resource<Painter> = asyncPainterResource(BASE_URL + subCategoryItem.imageUrl)
                KamelImage(
                    resource = image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color(0xFF6200EE), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$questionCount Qs",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(6.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = subCategoryItem.name,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.avatar),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Admin",
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TopCollectionCard(subCategoryItem: SubCategoriesItem, subQuestionsItems: List<SubQuestionsItem>) {
    val navigator = LocalNavigator.current
    val questionsForCategory = subQuestionsItems.filter { it.categoryId == subCategoryItem.id }
    val questionCount = questionsForCategory.size

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.width(200.dp)
            .clickable {
                navigator?.push(QuizScreenPlaySubScreen(subCategoryItem, questionsForCategory))
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                val image: Resource<Painter> = asyncPainterResource(BASE_URL + subCategoryItem.imageUrl)
                KamelImage(
                    resource = image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color(0xFF6200EE), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$questionCount Qs",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Column(
                    modifier = Modifier.wrapContentWidth()
                        .padding(6.dp)
                        .background(Color(0xFF6200EE), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.BottomStart),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = subCategoryItem.name,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}