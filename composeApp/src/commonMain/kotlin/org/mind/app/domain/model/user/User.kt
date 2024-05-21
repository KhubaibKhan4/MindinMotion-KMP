package org.mind.app.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val email: String,
    val password: String,
    val gender: String,
    val isStudent: Boolean
)
