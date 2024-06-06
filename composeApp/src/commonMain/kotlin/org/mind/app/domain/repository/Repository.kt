package org.mind.app.domain.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import org.mind.app.data.remote.MotionApiClient
import org.mind.app.data.repository.AuthService
import org.mind.app.domain.model.boards.Boards
import org.mind.app.domain.model.category.QuizCategoryItem
import org.mind.app.domain.model.chat.ChatMessage
import org.mind.app.domain.model.community.Community
import org.mind.app.domain.model.community.CommunityMessage
import org.mind.app.domain.model.community.UserProfile
import org.mind.app.domain.model.gemini.Gemini
import org.mind.app.domain.model.notes.Notes
import org.mind.app.domain.model.papers.Papers
import org.mind.app.domain.model.promotion.Promotions
import org.mind.app.domain.model.quiz.QuizQuestionsItem
import org.mind.app.domain.model.resume.ResumeItem
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
        auth.signOut()
    }

    override suspend fun uploadProfileImage(userId: Long, imageFile: ByteArray): String {
        return MotionApiClient.uploadProfileImage(userId, imageFile)
    }
    override suspend fun getAllResumes(): List<ResumeItem> {
        return MotionApiClient.getAllResumes()
    }
    fun getUserProfiles(): Flow<List<UserProfile>> = flow {
        val profilesRef = database.reference("userProfiles")
        profilesRef.valueEvents.collect { dataSnapshot ->
            val profiles = dataSnapshot.children.mapNotNull { it.value<UserProfile>() }
            emit(profiles)
        }
    }
    suspend fun uploadImageAndGetUrl(imageBytes: File, userEmail: String): String {
        return try {
            val storageRef = storage.reference.child("media/$userEmail/${Clock.System.now().toEpochMilliseconds()}.jpg")
            storageRef.putFile(imageBytes)
            storageRef.getDownloadUrl()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun sendMessagesBySocket(
        senderEmail: String,
        receiverEmail: String,
        message: String,
        imageUrl: String?
    ) {
        val chatMessage = ChatMessage(
            message = message,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            senderEmail = senderEmail,
            receiverEmail = receiverEmail,
            imageUrl = imageUrl
        )

        database.reference()
            .child("messages")
            .push()
            .setValue(chatMessage)
    }

    suspend fun sendCommunityMessage(
        communityId: String,
        senderEmail: String,
        message: String,
        imageUrl: String? = null
    ) {
        val communityMessage = CommunityMessage(
            message = message,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            senderEmail = senderEmail,
            communityId = communityId,
            imageUrl = imageUrl
        )
        database.reference()
            .child("communityMessages")
            .child(communityId)
            .push()
            .setValue(communityMessage)
    }

    suspend fun sendImageMessage(senderEmail: String, receiverEmail: String, imageBytes: File) {
        val userId = auth.currentUser?.uid.toString()
        val imageUrl = uploadImageAndGetUrl(imageBytes, userId)
        sendMessagesBySocket(senderEmail, receiverEmail, "", imageUrl)
    }

    suspend fun sendCommunityImageMessage(communityId: String, senderEmail: String, imageBytes: File) {
        val userId = auth.currentUser?.uid
        val imageUrl = uploadImageAndGetUrl(imageBytes, userId.toString())
        sendCommunityMessage(communityId, senderEmail, "", imageUrl)
    }


    override suspend fun getUsersByEmails(emails: List<String>): List<Users> {
        return MotionApiClient.getUsersByEmails(emails)
    }

    fun getMessages(): Flow<List<ChatMessage>> = flow {
        val messagesRef = database.reference("messages")
        messagesRef.valueEvents.collect { dataSnapshot ->
            val messages = dataSnapshot.children.mapNotNull { it.value<ChatMessage>() }
            emit(messages)
        }
    }
    suspend fun createCommunity(name: String, members: List<String>, admin: String) {
        val communityId = database.reference().child("communities").push().key ?: return
        val community = Community(id = communityId, name = name, members = members, admin = admin)
        database.reference().child("communities").child(communityId).setValue(community)
    }

    fun getCommunities(): Flow<List<Community>> = flow {
        val communitiesRef = database.reference("communities")
        communitiesRef.valueEvents.collect { dataSnapshot ->
            val communities = dataSnapshot.children.mapNotNull { it.value<Community>() }
            emit(communities)
        }
    }

    suspend fun sendCommunityMessage(communityId: String, senderEmail: String, message: String) {
        val communityMessage = CommunityMessage(
            message = message,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            senderEmail = senderEmail,
            communityId = communityId
        )
        database.reference()
            .child("communityMessages")
            .child(communityId)
            .push()
            .setValue(communityMessage)
    }

    fun getCommunityMessages(communityId: String): Flow<List<CommunityMessage>> = flow {
        val messagesRef = database.reference("communityMessages").child(communityId)
        messagesRef.valueEvents.collect { dataSnapshot ->
            val messages = dataSnapshot.children.mapNotNull { it.value<CommunityMessage>() }
            emit(messages)
        }
    }
    suspend fun addUserToCommunity(communityId: String, userEmail: String) {
        val membersRef = database.reference("communities").child(communityId).child("members")
        val currentMembersSnapshot = membersRef.valueEvents.first()
        val currentMembers = currentMembersSnapshot.value<List<String>>()?.toMutableList() ?: mutableListOf()

        if (!currentMembers.contains(userEmail)) {
            currentMembers.add(userEmail)
            membersRef.setValue(currentMembers)
        }
    }



    suspend fun removeUserFromCommunity(communityId: String, userEmail: String) {
        val membersRef = database.reference("communities").child(communityId).child("members")
        val currentMembersSnapshot = membersRef.valueEvents.first()
        val currentMembers = currentMembersSnapshot.value<List<String>>()?.toMutableList() ?: mutableListOf()

        if (currentMembers.contains(userEmail)) {
            currentMembers.remove(userEmail)
            membersRef.setValue(currentMembers)
        }
    }


    fun getCommunityUsers(communityId: String): Flow<List<String>> = flow {
        val communityRef = database.reference("communities").child(communityId).child("members")
        communityRef.valueEvents.collect { dataSnapshot ->
            val users = dataSnapshot.value<List<String>>() ?: emptyList()
            emit(users)
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