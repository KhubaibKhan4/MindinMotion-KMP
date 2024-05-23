package org.mind.app.domain.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mind.app.data.repository.AuthService
import org.mind.app.domain.model.user.User

class Repository(
    val auth: FirebaseAuth,
    val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : AuthService {
    override val currentUserId: String
        get() = auth.currentUser?.uid.toString()

    override val isAuthenticated: Boolean
        get() = auth.currentUser != null && auth.currentUser?.isAnonymous == false

    override val currentUser: Flow<User> =
        auth.authStateChanged.map { it?.let { User(it.uid, it.isAnonymous) } ?: User() }
    private suspend fun launchWithAwait(block : suspend  () -> Unit) {
        scope.async {
            block()
        }.await()
    }


    override suspend fun authenticate(email: String, password: String) {
        scope.async {
            auth.signInWithEmailAndPassword(email, password)
        }
    }

    override suspend fun createUser(email: String, password: String) {
        launchWithAwait {
            auth.createUserWithEmailAndPassword(email, password)
        }
    }

    override suspend fun signOut() {
        if (auth.currentUser?.isAnonymous==true){
            auth.currentUser?.delete()
        }
    }
}