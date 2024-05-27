package org.mind.app.presentation.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

fun parseMessageText(text: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = text.split("\n")
        var isCodeBlock = false
        lines.forEachIndexed { index, line ->
            var formattedLine = line.replace("**", "").replace("*", "")
            if (index != lines.lastIndex) {
                formattedLine += "\n"
            }

            when {
                formattedLine.startsWith("### ") -> {
                    val heading = formattedLine.removePrefix("### ")
                    append(heading)
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        start = length - heading.length,
                        end = length
                    )
                }

                formattedLine.startsWith("## ") -> {
                    val heading = formattedLine.removePrefix("## ")
                    append(heading)
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        start = length - heading.length,
                        end = length
                    )
                }

                formattedLine.startsWith("# ") -> {
                    val heading = formattedLine.removePrefix("# ")
                    append(heading)
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        start = length - heading.length,
                        end = length
                    )
                }

                formattedLine.startsWith("***") && formattedLine.endsWith("***") -> {
                    val boldItalicText = formattedLine.removeSurrounding("***")
                    append(boldItalicText)
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        ),
                        start = length - boldItalicText.length,
                        end = length
                    )
                }

                formattedLine.startsWith("**") && formattedLine.endsWith("**") -> {
                    val boldText = formattedLine.removeSurrounding("**")
                    append(boldText)
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        ),
                        start = length - boldText.length,
                        end = length
                    )
                }

                formattedLine.startsWith("*") && formattedLine.endsWith("*") -> {
                    val italicText = formattedLine.removeSurrounding("*")
                    append(italicText)
                    addStyle(
                        style = SpanStyle(
                            fontStyle = FontStyle.Italic
                        ),
                        start = length - italicText.length,
                        end = length
                    )
                }

                formattedLine.startsWith("~~") && formattedLine.endsWith("~~") -> {
                    val strikethroughText = formattedLine.removeSurrounding("~~")
                    append(strikethroughText)
                    addStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.LineThrough
                        ),
                        start = length - strikethroughText.length,
                        end = length
                    )
                }

                formattedLine.startsWith("• ") -> {
                    append(formattedLine)
                }

                formattedLine.startsWith("* ") -> {
                    val bulletPoint = formattedLine.removePrefix("* ")
                    append("• $bulletPoint")
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Normal
                        ),
                        start = length - bulletPoint.length - 2,
                        end = length
                    )
                }

                formattedLine.startsWith("- [x] ") -> {
                    val taskText = formattedLine.removePrefix("- [x] ")
                    append("☑ $taskText")
                    addStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.LineThrough
                        ),
                        start = length - taskText.length - 2,
                        end = length
                    )
                }

                formattedLine.startsWith("- [ ] ") -> {
                    val taskText = formattedLine.removePrefix("- [ ] ")
                    append("☐ $taskText")
                }

                formattedLine.matches(Regex("^\\d+\\. .*")) -> {
                    append(formattedLine)
                }

                formattedLine.contains("• ") -> {
                    val parts = formattedLine.split("• ")
                    val bulletPoint = parts[0] + "• "
                    val boldText = parts[1]
                    append(bulletPoint)
                    append(boldText)
                    addStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        ),
                        start = length - boldText.length,
                        end = length
                    )
                }
                formattedLine.startsWith("```") && formattedLine.endsWith("```") -> {
                    val codeText = formattedLine.removeSurrounding("```")
                    append(codeText)
                    addStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = Color.Red,
                        ),
                        start = length - codeText.length,
                        end = length
                    )
                }

                else -> {
                    append(formattedLine)
                }
            }
            isCodeBlock = isCodeBlock || formattedLine.startsWith("```")
            isCodeBlock = isCodeBlock && !formattedLine.endsWith("```")
        }
    }
}