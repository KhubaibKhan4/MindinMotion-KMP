package org.mind.app.data.repository

import org.mind.app.domain.model.user.User

interface GoogleApi {
    suspend fun createUser(email:String, password: String)
}