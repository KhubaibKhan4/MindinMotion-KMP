package org.mind.app.domain.model.papers


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Classe(
    @SerialName("boardId")
    val boardId: Int = 0,
    @SerialName("description")
    val description: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("subjects")
    val subjects: List<Subject> = listOf(),
    @SerialName("title")
    val title: String = ""
)