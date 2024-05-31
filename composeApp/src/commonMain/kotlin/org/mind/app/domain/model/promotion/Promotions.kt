package org.mind.app.domain.model.promotion


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Promotions(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("imageUrl")
    val imageUrl: String = ""
)