package org.mind.app.presentation.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mind.app.domain.model.category.QuizCategoryItem
import org.mind.app.domain.model.quiz.QuizQuestionsItem
import org.mind.app.presentation.ui.tabs.quiz.QuizTab

class QuizQuestionsScreen(
    private val quizCategoryItem: QuizCategoryItem,
    private val quizQuestionsItem: List<QuizQuestionsItem>,
) : Screen {
    @Composable
    override fun Content() {
        QuizQuestionsScreenContent(quizCategoryItem, quizQuestionsItem)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizQuestionsScreenContent(
    quizCategoryItem: QuizCategoryItem,
    quizQuestionsItem: List<QuizQuestionsItem>,
) {
    val navigator = LocalTabNavigator.current

    var timer by remember { mutableStateOf(60) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf(-1) }
    var correctAnswers by remember { mutableStateOf(0) }
    var wrongAnswers by remember { mutableStateOf(0) }
    var answeredCorrectly by remember { mutableStateOf<Boolean?>(null) }
    val answeredQuestions = remember { mutableStateMapOf<Int, Boolean>() }

    val scope = rememberCoroutineScope()

    LaunchedEffect(currentQuestionIndex) {
        if (currentQuestionIndex < quizQuestionsItem.size) {
            timer = 60
            while (timer > 0) {
                delay(1000)
                timer--
            }
            if (timer <= 0) {
                answeredCorrectly = false
                wrongAnswers++
                answeredQuestions[currentQuestionIndex] = false
                delay(1000)
                moveToNextQuestion(
                    currentQuestionIndex,
                    setCurrentQuestionIndex = { currentQuestionIndex = it },
                    resetAnswerState = {
                        selectedAnswerIndex = -1
                        answeredCorrectly = null
                    }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Icon(
                        Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            navigator.current = QuizTab
                        }
                    )
                },
                title = {
                    if (currentQuestionIndex < quizQuestionsItem.size) Text("Time Left: $timer seconds") else Text(
                        ""
                    )
                },
                actions = {
                    Button(
                        onClick = {
                            navigator.current = QuizTab
                        },
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Submit")
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Blue,
                            Color.Black
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = it.calculateTopPadding(), start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    quizQuestionsItem.forEachIndexed { index, question ->
                        val isAnswered = answeredQuestions.containsKey(index)
                        val isCorrect = answeredQuestions[index] ?: false
                        val icon = if (isAnswered) {
                            if (isCorrect) Icons.Default.Check else Icons.Default.Close
                        } else null

                        item {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = if (index == currentQuestionIndex) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (icon != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .border(
                                                width = 1.dp,
                                                color = if (isCorrect) Color.Green else Color.Red,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isCorrect) Color.Green else Color.Red
                                        )
                                    }
                                } else {
                                    Text(
                                        text = (index + 1).toString(),
                                        color = if (index == currentQuestionIndex) Color.Blue else Color.White
                                    )
                                }
                            }
                        }
                    }
                }




                if (currentQuestionIndex < quizQuestionsItem.size) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .background(Color.LightGray)
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Question ${currentQuestionIndex + 1}",
                                    style = MaterialTheme.typography.headlineLarge
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "20",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Icon(
                                        imageVector = Icons.Default.AttachMoney,
                                        contentDescription = null,
                                        tint = Color.Yellow,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }

                            QuizQuestionItem(
                                question = quizQuestionsItem[currentQuestionIndex],
                                onAnswerSelected = { isCorrect ->
                                    answeredCorrectly = isCorrect
                                    answeredQuestions[currentQuestionIndex] = isCorrect
                                    if (isCorrect) correctAnswers++ else wrongAnswers++
                                    scope.launch {
                                        delay(1000)
                                        moveToNextQuestion(
                                            currentQuestionIndex,
                                            setCurrentQuestionIndex = {
                                                currentQuestionIndex = it
                                            },
                                            resetAnswerState = {
                                                selectedAnswerIndex = -1
                                                answeredCorrectly = null
                                            }
                                        )
                                    }
                                },
                                selectedAnswerIndex = selectedAnswerIndex,
                                answeredCorrectly = answeredCorrectly,
                                setSelectedAnswerIndex = { selectedAnswerIndex = it }
                            )
                        }
                    }
                } else {
                    showResult(correctAnswers, wrongAnswers, onNewQuizClick = {
                        resetQuiz(
                            setCurrentQuestionIndex = { currentQuestionIndex = it },
                            resetAnswerState = {
                                selectedAnswerIndex = -1
                                answeredCorrectly = null
                            },
                            resetScore = {
                                correctAnswers = 0
                                wrongAnswers = 0
                                answeredQuestions.clear()
                            }
                        )
                    })
                }
            }
        }
    }
}

@Composable
fun QuizQuestionItem(
    question: QuizQuestionsItem,
    onAnswerSelected: (Boolean) -> Unit,
    selectedAnswerIndex: Int,
    answeredCorrectly: Boolean?,
    setSelectedAnswerIndex: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    ) {
        Text(
            text = question.title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val answerOptions = listOf(
            "A. ${question.answer1}",
            "B. ${question.answer2}",
            "C. ${question.answer3}",
            "D. ${question.answer4}"
        )

        answerOptions.forEachIndexed { index, answer ->
            OutlinedButton(
                onClick = {
                    setSelectedAnswerIndex(index)
                    onAnswerSelected(answer.drop(3) == question.correctAnswer) // drop the "A. ", "B. ", etc.
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedAnswerIndex == index) {
                        if (answeredCorrectly != null) {
                            if (answeredCorrectly) Color.Green else Color.Red
                        } else Color.Transparent
                    } else Color.Transparent,
                    contentColor = if (selectedAnswerIndex == index) {
                        if (answeredCorrectly != null) {
                            if (answeredCorrectly) Color.White else Color.White
                        } else Color.Black
                    } else Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = answer,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        answeredCorrectly?.let {
            Text(
                text = if (it) "Correct!" else "Wrong!",
                color = if (it) Color.Green else Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun showResult(correctAnswers: Int, wrongAnswers: Int, onNewQuizClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (correctAnswers >= wrongAnswers) Icons.Default.Check else Icons.Default.Error,
                contentDescription = null,
                tint = if (correctAnswers >= wrongAnswers) Color.Green else Color.Red,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Results",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Total Correct Answers: $correctAnswers",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Total Wrong Answers: $wrongAnswers",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            val totalQuestions = correctAnswers + wrongAnswers
            val percentage = if (totalQuestions > 0) {
                (correctAnswers * 100) / totalQuestions
            } else {
                0
            }
            val message = if (percentage >= 60) {
                "Congratulations! You passed!"
            } else {
                "You need to improve. Keep practicing!"
            }
            Text(
                text = "Percentage: $percentage%",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = onNewQuizClick,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Play New Quiz")
            }
        }
    }
}


fun moveToNextQuestion(
    currentQuestionIndex: Int,
    setCurrentQuestionIndex: (Int) -> Unit,
    resetAnswerState: () -> Unit,
) {
    setCurrentQuestionIndex(currentQuestionIndex + 1)
    resetAnswerState()
}

fun resetQuiz(
    setCurrentQuestionIndex: (Int) -> Unit,
    resetAnswerState: () -> Unit,
    resetScore: () -> Unit,
) {
    setCurrentQuestionIndex(0)
    resetAnswerState()
    resetScore()
}