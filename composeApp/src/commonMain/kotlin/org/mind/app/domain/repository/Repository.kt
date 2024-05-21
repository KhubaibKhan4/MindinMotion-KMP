package org.mind.app.domain.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.initialize
import org.mind.app.data.repository.GoogleApi

class Repository : GoogleApi {
    override suspend fun createUser(email: String, password: String) {
        Firebase.initialize(this)
        Firebase.auth.createUserWithEmailAndPassword(email, password)
    }
}