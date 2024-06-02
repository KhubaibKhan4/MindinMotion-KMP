package org.mind.app.domain.model.community

import kotlinx.serialization.Serializable

@Serializable
data class Community(
    val id: String = "",
    val name: String,
    val members: List<String>,
    val admin: String
)
@Serializable
data class CommunityMessage(
    val message: String,
    val timestamp: Long,
    val senderEmail: String,
    val communityId: String
)
@Serializable
data class UserProfile(
    val email: String = "",
    val name: String = "",
    val profilePictureUrl: String = ""
)