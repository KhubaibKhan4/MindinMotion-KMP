package org.mind.app.domain.model.subcategories


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubCategoriesItem(
    @SerialName("categoryName")
    val categoryName: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("imageUrl")
    val imageUrl: String = "",
    @SerialName("name")
    val name: String = ""
)