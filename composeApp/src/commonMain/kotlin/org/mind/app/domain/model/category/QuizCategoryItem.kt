package org.mind.app.domain.model.category


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuizCategoryItem(
    @SerialName("description")
    val description: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("imageUrl")
    val imageUrl: String = "",
    @SerialName("name")
    val name: String = ""
)