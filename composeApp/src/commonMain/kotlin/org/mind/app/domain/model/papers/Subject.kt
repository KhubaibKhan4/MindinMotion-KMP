package org.mind.app.domain.model.papers


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    @SerialName("classId")
    val classId: Int = 0,
    @SerialName("description")
    val description: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("papers")
    val papers: List<Paper>? = null,
    @SerialName("title")
    val title: String = ""
)