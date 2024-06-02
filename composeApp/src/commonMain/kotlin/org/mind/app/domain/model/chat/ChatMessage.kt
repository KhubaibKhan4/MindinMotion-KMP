package org.mind.app.domain.model.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(val message: String, val timestamp: Long, val senderEmail: String)