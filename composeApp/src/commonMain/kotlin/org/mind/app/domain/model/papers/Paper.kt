package org.mind.app.domain.model.papers


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Paper(
    @SerialName("boardId")
    val boardId: Int = 0,
    @SerialName("classId")
    val classId: Int = 0,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("pdfUrl")
    val pdfUrl: String = "",
    @SerialName("subjectId")
    val subjectId: Int = 0
)