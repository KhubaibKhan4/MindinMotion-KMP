package org.mind.app.domain.model.gemini

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Part(
    @SerialName("text")
    val text: String
)
