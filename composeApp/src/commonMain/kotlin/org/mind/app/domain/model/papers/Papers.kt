package org.mind.app.domain.model.papers


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Papers(
    @SerialName("classes")
    val classes: List<Classe> = listOf(),
    @SerialName("description")
    val description: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("imageUrl")
    val imageUrl: String = "",
    @SerialName("title")
    val title: String = ""
)