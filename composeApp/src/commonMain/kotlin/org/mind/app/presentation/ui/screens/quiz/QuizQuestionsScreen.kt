package org.mind.app.presentation.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    val scope = rememberCoroutineScope()

    LaunchedEffect(currentQuestionIndex) {
        timer = 60
        while (timer > 0) {
            delay(1000)
            timer--
        }
        if (timer <= 0) {
            answeredCorrectly = false
            wrongAnswers++
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(quizCategoryItem.name) },
                navigationIcon = {
                    Icon(
                        Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            navigator.current = QuizTab
                        }
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = it.calculateTopPadding(), start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "Time Left: $timer seconds",
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (currentQuestionIndex < quizQuestionsItem.size) {
                QuizQuestionItem(
                    question = quizQuestionsItem[currentQuestionIndex],
                    onAnswerSelected = { isCorrect ->
                        answeredCorrectly = isCorrect
                        if (isCorrect) correctAnswers++ else wrongAnswers++
                        scope.launch {
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
                    },
                    selectedAnswerIndex = selectedAnswerIndex,
                    answeredCorrectly = answeredCorrectly,
                    setSelectedAnswerIndex = { selectedAnswerIndex = it }
                )
            } else {
                showResult(correctAnswers, wrongAnswers)
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
    setSelectedAnswerIndex: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = question.title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val answerOptions = listOf(question.answer1, question.answer2, question.answer3, question.answer4)

        answerOptions.forEachIndexed { index, answer ->
            AnswerRadioButton(
                text = answer,
                selected = selectedAnswerIndex == index,
                onClick = {
                    setSelectedAnswerIndex(index)
                    onAnswerSelected(answer == question.correctAnswer)
                },
                backgroundColor = if (selectedAnswerIndex == index) {
                    if (answeredCorrectly != null) {
                        if (answeredCorrectly) Color.Green else Color.Red
                    } else Color.Transparent
                } else Color.Transparent,
                modifier = Modifier.padding(vertical = 4.dp)
            )
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
fun AnswerRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    backgroundColor: Color = Color.Transparent,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(8.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = text,
            modifier = Modifier.clickable(onClick = onClick)
        )
    }
}

@Composable
fun showResult(correctAnswers: Int, wrongAnswers: Int) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Results",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Total Correct Answers: $correctAnswers",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Total Wrong Answers: $wrongAnswers",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

fun moveToNextQuestion(
    currentQuestionIndex: Int,
    setCurrentQuestionIndex: (Int) -> Unit,
    resetAnswerState: () -> Unit
) {
    setCurrentQuestionIndex(currentQuestionIndex + 1)
    resetAnswerState()
}