package org.mind.app.data.repository

import kotlinx.coroutines.flow.Flow
import org.mind.app.domain.model.boards.Boards
import org.mind.app.domain.model.category.QuizCategoryItem
import org.mind.app.domain.model.gemini.Gemini
import org.mind.app.domain.model.notes.Notes
import org.mind.app.domain.model.papers.Papers
import org.mind.app.domain.model.promotion.Promotions
import org.mind.app.domain.model.quiz.QuizQuestionsItem
import org.mind.app.domain.model.subcategories.SubCategoriesItem
import org.mind.app.domain.model.subquestions.SubQuestionsItem
import org.mind.app.domain.model.user.User
import org.mind.app.domain.model.users.Users

interface AuthService {
    val currentUserId: String
    val isAuthenticated: Boolean

    val currentUser: Flow<User>

    suspend fun authenticate(email: String, password: String)
    suspend fun createUser(email: String, password: String)
    suspend fun resetPassword(email: String)
    suspend  fun sendMessagesBySocket(
        senderEmail: String,
        receiverEmail: String,
        message: String,
        imageUrl: String? = null
    )
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

    suspend fun getUsersById(userId: Int): Users
    suspend fun updateUserDetails(
        userId: Int,
        email: String,
        username: String,
        fullName: String,
        address: String,
        city: String,
        country: String,
        postalCode: String,
        phoneNumber: String,
    )

    suspend fun loginServerUser(
        email: String,
        password: String,
    ): Users

    suspend fun getUserByEmail(email: String): Users
    suspend fun generateContent(content: String): Gemini
    suspend fun getAllCategories(): List<QuizCategoryItem>
    suspend fun getAllQuizzes(): List<QuizQuestionsItem>
    suspend fun getAllNotes(): List<Notes>
    suspend fun getAllBoards(): List<Boards>
    suspend fun getAllPapersWithDetail(id: Long): Papers
    suspend fun getAllSubCategories(): List<SubCategoriesItem>
    suspend fun getSubQuestions(): List<SubQuestionsItem>
    suspend fun getAllPromotions(): List<Promotions>
    suspend fun getAllUsers(): List<Users>
    suspend fun getUsersByEmails(emails: List<String>): List<Users>
}