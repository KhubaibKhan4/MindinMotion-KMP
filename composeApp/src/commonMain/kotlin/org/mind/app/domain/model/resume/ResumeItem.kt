package org.mind.app.domain.model.resume


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResumeItem(
    @SerialName("categoryName")
    val categoryName: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("imageUrl")
    val imageUrl: String = ""
)