package org.mind.app.domain.model.gemini

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Gemini(
    @SerialName("candidates")
    val candidates: List<Candidate>? =  null,
    @SerialName("promptFeedback")
    val promptFeedback: PromptFeedback? = null
)
