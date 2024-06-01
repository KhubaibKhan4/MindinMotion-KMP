package org.mind.app.domain.model.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val sender: String,
    val content: String,
    val timestamp: Long,
)