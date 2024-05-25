package org.mind.app.domain.model.users

import kotlinx.serialization.Serializable

@Serializable
data class Users(
    val id: Long,
    val username: String,
    val email: String,
    val password: String,
    val fullName: String,
    val address: String,
    val city: String,
    val country: String,
    val postalCode: Long,
    val phoneNumber: String,
    val userRole: String,
    val profileImage: String?
)
