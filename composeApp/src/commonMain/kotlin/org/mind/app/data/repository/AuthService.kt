package org.mind.app.data.repository

import kotlinx.coroutines.flow.Flow
import org.mind.app.domain.model.user.User

interface AuthService {
    val currentUserId: String
    val isAuthenticated: Boolean

    val currentUser: Flow<User>

    suspend fun authenticate(email: String, password: String)
    suspend fun createUser(email: String, password: String)
    suspend fun resetPassword(email: String)

    suspend fun signOut()
    suspend fun signUpUser(
        email: String,
        password: String,
        username: String,
        fullName: String,
        address: String,
        city: String,
        country: String,
        postalCode: String,
        phoneNumber: String,
        userRole: String,
    ): String
}