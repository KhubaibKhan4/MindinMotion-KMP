package org.mind.app.domain.model.subquestions


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubQuestionsItem(
    @SerialName("answer1")
    val answer1: String = "",
    @SerialName("answer2")
    val answer2: String = "",
    @SerialName("answer3")
    val answer3: String = "",
    @SerialName("answer4")
    val answer4: String = "",
    @SerialName("categoryId")
    val categoryId: Int = 0,
    @SerialName("categoryTitle")
    val categoryTitle: String = "",
    @SerialName("correctAnswer")
    val correctAnswer: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("title")
    val title: String = ""
)