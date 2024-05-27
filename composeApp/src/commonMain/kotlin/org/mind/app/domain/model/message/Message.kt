package org.mind.app.domain.model.message

data class Message(
    val text: String,
    val isUserMessage: Boolean,
    val isLoading: Boolean = false,
    var showTypewriterEffect: Boolean = false
)