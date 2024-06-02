package org.mind.app.domain.repository

import com.eygraber.uri.Uri
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.FirebaseStorage
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.mind.app.data.remote.MotionApiClient
import org.mind.app.data.repository.AuthService
import org.mind.app.domain.model.boards.Boards
import org.mind.app.domain.model.category.QuizCategoryItem
import org.mind.app.domain.model.chat.ChatMessage
import org.mind.app.domain.model.chat.MessageType
import org.mind.app.domain.model.gemini.Gemini
import org.mind.app.domain.model.notes.Notes
import org.mind.app.domain.model.papers.Papers
import org.mind.app.domain.model.promotion.Promotions
import org.mind.app.domain.model.quiz.QuizQuestionsItem
import org.mind.app.domain.model.subcategories.SubCategoriesItem
import org.mind.app.domain.model.subquestions.SubQuestionsItem
import org.mind.app.domain.model.user.User
import org.mind.app.domain.model.users.Users

class Repository(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage
) : AuthService {
    override val currentUserId: String
        get() = auth.currentUser?.uid.toString()

    override val isAuthenticated: Boolean
        get() = auth.currentUser != null && auth.currentUser?.isAnonymous == false

    override val currentUser: Flow<User> =
        auth.authStateChanged.map { it?.let { User(it.uid, it.isAnonymous) } ?: User() }

    override suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
    }

    override suspend fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
    }

    override suspend fun signOut() {
        if (auth.currentUser?.isAnonymous == true) {
            auth.currentUser?.delete()
        }
    }

    override suspend fun sendMessagesBySocket(
        senderEmail: String,
        receiverEmail: String,
        message: String,
    ) {
        val chatMessage = ChatMessage(
            message = message,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            senderEmail = senderEmail,
            receiverEmail = receiverEmail
        )

        database.reference()
            .child("messages")
            .push()
            .setValue(chatMessage)
    }

    fun getMessages(): Flow<List<ChatMessage>> = flow {
        val messagesRef = database.reference("messages")
        messagesRef.valueEvents.collect { dataSnapshot ->
            val messages = dataSnapshot.children.mapNotNull { it.value<ChatMessage>() }
            emit(messages)
        }
    }

    override suspend fun signUpUser(
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
    ): String {
        return MotionApiClient.signUpUser(
            email = email,
            password = password,
            username = username,
            fullName = fullName,
            address = address,
            city = city,
            country = country,
            postalCode = postalCode,
            phoneNumber = phoneNumber,
            userRole = userRole
        )
    }

    override suspend fun getUsersById(userId: Int): Users {
        return MotionApiClient.getUsersById(userId)
    }

    override suspend fun updateUserDetails(
        userId: Int,
        email: String,
        username: String,
        fullName: String,
        address: String,
        city: String,
        country: String,
        postalCode: String,
        phoneNumber: String,
    ) {
        return MotionApiClient.updateUserDetails(
            userId,
            email,
            username,
            fullName,
            address,
            city,
            country,
            postalCode,
            phoneNumber
        )
    }

    override suspend fun loginServerUser(email: String, password: String): Users {
        return MotionApiClient.loginServerUser(email, password)
    }

    override suspend fun getUserByEmail(email: String): Users {
        return MotionApiClient.getUserByEmail(email)
    }

    override suspend fun generateContent(content: String): Gemini {
        return MotionApiClient.generateContent(content)
    }

    override suspend fun getAllCategories(): List<QuizCategoryItem> {
        return MotionApiClient.getAllCategories()
    }

    override suspend fun getAllQuizzes(): List<QuizQuestionsItem> {
        return MotionApiClient.getAllQuizzes()
    }

    override suspend fun getAllNotes(): List<Notes> {
        return MotionApiClient.getAllNotes()
    }

    override suspend fun getAllBoards(): List<Boards> {
        return MotionApiClient.getAllBoards()
    }

    override suspend fun getAllPapersWithDetail(id: Long): Papers {
        return MotionApiClient.getAllPapersWithDetail(id)
    }

    override suspend fun getAllSubCategories(): List<SubCategoriesItem> {
        return MotionApiClient.getAllSubCategories()
    }

    override suspend fun getSubQuestions(): List<SubQuestionsItem> {
        return MotionApiClient.getSubQuestions()
    }

    override suspend fun getAllPromotions(): List<Promotions> {
        return MotionApiClient.getAllPromotions()
    }

    override suspend fun getAllUsers(): List<Users> {
        return MotionApiClient.getAllUsers()
    }
}