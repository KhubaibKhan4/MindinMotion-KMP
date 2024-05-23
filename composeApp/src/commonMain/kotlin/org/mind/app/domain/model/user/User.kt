package org.mind.app.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val isAnonymous: Boolean = true
)
