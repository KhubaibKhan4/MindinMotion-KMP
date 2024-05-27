package org.mind.app.domain.model.gemini

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromptFeedback(
    @SerialName("safetyRatings")
    val safetyRatings: List<SafetyRating>
)
