package org.mind.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.mind.app.data.repository.AuthService
import org.mind.app.domain.model.user.User

class Repository(
   // private val auth: FirebaseAuth,
) : AuthService {
    //    override val currentUserId: String
//        get() = auth.currentUser?.uid.toString()
//
//    override val isAuthenticated: Boolean
//        get() = auth.currentUser != null && auth.currentUser?.isAnonymous == false
//
//    override val currentUser: Flow<User> =
//        auth.authStateChanged.map { it?.let { User(it.uid, it.isAnonymous) } ?: User() }
//
//    override suspend fun authenticate(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//    }
//
//    override suspend fun createUser(email: String, password: String) {
//        auth.createUserWithEmailAndPassword(email, password)
//    }
//
//    override suspend fun signOut() {
//        if (auth.currentUser?.isAnonymous == true) {
//            auth.currentUser?.delete()
//        }
//    }
    override val currentUserId: String
        get() = TODO("Not yet implemented")
    override val isAuthenticated: Boolean
        get() = TODO("Not yet implemented")
    override val currentUser: Flow<User>
        get() = TODO("Not yet implemented")

    override suspend fun authenticate(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun createUser(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun signOut() {
        TODO("Not yet implemented")
    }
}