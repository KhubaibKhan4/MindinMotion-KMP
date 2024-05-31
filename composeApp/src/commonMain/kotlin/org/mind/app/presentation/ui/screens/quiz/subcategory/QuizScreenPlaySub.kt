package org.mind.app.presentation.ui.screens.quiz.subcategory

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mind_in_motion.composeapp.generated.resources.Res
import mind_in_motion.composeapp.generated.resources.bitcoins
import org.jetbrains.compose.resources.painterResource
import org.mind.app.domain.model.category.QuizCategoryItem
import org.mind.app.domain.model.quiz.QuizQuestionsItem
import org.mind.app.domain.model.subcategories.SubCategoriesItem
import org.mind.app.domain.model.subquestions.SubQuestionsItem
import org.mind.app.notify
import org.mind.app.presentation.ui.tabs.home.HomeTab
import org.mind.app.presentation.ui.tabs.quiz.QuizQuestions
import org.mind.app.presentation.ui.tabs.quiz.QuizTab
import org.mind.app.theme.LocalThemeIsDark

class QuizScreenPlaySubScreen(
    private val quizCategoryItem: SubCategoriesItem,
    private val quizQuestionsItem: List<SubQuestionsItem>,
) : Screen {
    @Composable
    override fun Content() {
        QuizScreenPlayContentSub(quizCategoryItem, quizQuestionsItem)
    }
}

@Composable
fun QuizScreenPlayContentSub(
    quizCategoryItem: SubCategoriesItem,
    quizQuestionsItem: List<SubQuestionsItem>,
) {
    val isDark by LocalThemeIsDark.current
    val navigator = LocalTabNavigator.current
    val localNavigator = LocalNavigator.current
    var isNoQuizAvailable by remember { mutableStateOf(false) }
    val currentDay = remember {
        val currentMoment = Clock.System.now()
        val currentDate = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dayOfWeek = currentDate.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        dayOfWeek
    }
    LazyColumn {
        item {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0000FF),
                                Color(0xFF8A2BE2)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(100.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Yellow
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Yellow
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Yellow
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.Yellow)
                            ) {
                                Text(
                                    text = "200",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.bitcoins),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(240.dp)
                                    .weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$currentDay's",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            fontSize = 38.sp
                        )
                        Text(
                            text = "Super Quiz",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            fontSize = 55.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            buildAnnotatedString {
                                append("Play Super Quiz & earn ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("200")
                                }
                                append(" Coins")
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardColors(
                            containerColor = if(isDark) Color.Black else Color.White,
                            contentColor = if (isDark) Color.White else Color.Black,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.Transparent
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                text = "Today's Quiz on",
                                style = MaterialTheme.typography.bodyLarge,
                                color =if (isDark) Color.White else Color.Black
                            )
                            Text(
                                text = quizCategoryItem.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.Blue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 38.sp
                            )
                            Text(
                                text = quizCategoryItem.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isDark) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF0000FF),
                                                Color(0xFF8A2BE2)
                                            )
                                        )
                                    )
                                    .clickable {
                                        if(quizQuestionsItem.isEmpty()){
                                            isNoQuizAvailable = !isNoQuizAvailable
                                        }else {
                                            navigator.current =
                                                QuizQuestionsSub(
                                                    quizCategoryItem,
                                                    quizQuestionsItem
                                                )
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isNoQuizAvailable){
                                    notify("No Quizzes Available")
                                }
                                Text(
                                    text = "Play QUIZ NOW",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${quizQuestionsItem.size} Questions",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isDark) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(start = 6.dp, top = 20.dp)
                        .size(30.dp)
                        .align(Alignment.TopStart)
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            localNavigator?.pop()
                        },
                        tint = Color.White
                    )
                }
            }
        }
    }
}
