package org.mind.app.presentation.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TypewriterEffect(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    typingDelay: Long = 50L,
    blinkDelay: Long = 500L,
) {
    var visibleText by remember { mutableStateOf(AnnotatedString("")) }
    var isTypingFinished by remember { mutableStateOf(false) }

    LaunchedEffect(text) {
        text.forEachIndexed { index, _ ->
            visibleText = text.subSequence(0, index + 1) as AnnotatedString
            delay(typingDelay)
        }
        isTypingFinished = true
    }

    LaunchedEffect(isTypingFinished) {
        while (!isTypingFinished) {
            delay(blinkDelay)
            visibleText = if (visibleText.isNotEmpty() && visibleText.text.last() == '█') {
                AnnotatedString(
                    visibleText.text.dropLast(1),
                    visibleText.spanStyles,
                    visibleText.paragraphStyles
                )
            } else {
                AnnotatedString(
                    visibleText.text + "█",
                    visibleText.spanStyles,
                    visibleText.paragraphStyles
                )
            }
        }
    }

    Text(
        text = if (isTypingFinished) visibleText else AnnotatedString(
            visibleText.text + "█",
            visibleText.spanStyles,
            visibleText.paragraphStyles
        ),
        modifier = modifier,
        color = Color.White,
        fontSize = 16.sp
    )
}