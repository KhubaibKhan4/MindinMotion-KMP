package org.mind.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.storage.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.mind.app.data.local.DatabaseHelper
import org.mind.app.domain.model.boards.Boards
import org.mind.app.domain.model.category.QuizCategoryItem
import org.mind.app.domain.model.chat.ChatMessage
import org.mind.app.domain.model.community.Community
import org.mind.app.domain.model.community.CommunityMessage
import org.mind.app.domain.model.community.UserProfile
import org.mind.app.domain.model.gemini.Gemini
import org.mind.app.domain.model.message.Message
import org.mind.app.domain.model.notes.Notes
import org.mind.app.domain.model.papers.Papers
import org.mind.app.domain.model.promotion.Promotions
import org.mind.app.domain.model.quiz.QuizQuestionsItem
import org.mind.app.domain.model.subcategories.SubCategoriesItem
import org.mind.app.domain.model.subquestions.SubQuestionsItem
import org.mind.app.domain.model.user.User
import org.mind.app.domain.model.users.Users
import org.mind.app.domain.repository.Repository
import org.mind.app.domain.usecases.ResultState

class MainViewModel(
    private val repository: Repository,
    private val databaseHelper: DatabaseHelper,
) : ViewModel() {
    private val _loginUser = MutableStateFlow<ResultState<String>>(ResultState.Loading)
    val loginUser = _loginUser.asStateFlow()

    private val _createUser = MutableStateFlow<ResultState<String>>(ResultState.Loading)
    val createUser = _createUser.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _resetPasswordState = MutableStateFlow<ResultState<String>>(ResultState.Loading)
    val resetPasswordState = _resetPasswordState.asStateFlow()

    private val _signOutState = MutableStateFlow<ResultState<String>>(ResultState.Loading)
    val signOutState = _signOutState.asStateFlow()

    private val _signupUsersServer = MutableStateFlow<ResultState<String>>(ResultState.Loading)
    val signupUsersServer = _signupUsersServer.asStateFlow()

    private val _userDetail = MutableStateFlow<ResultState<Users>>(ResultState.Loading)
    val userDetail = _userDetail.asStateFlow()

    private val _updateUserDetails = MutableStateFlow<ResultState<String>>(ResultState.Loading)
    val updateUserDetails = _updateUserDetails.asStateFlow()

    private val _loginServer = MutableStateFlow<ResultState<Users>>(ResultState.Loading)
    val loginServer = _loginServer.asStateFlow()

    private val _userByEmail = MutableStateFlow<ResultState<Users>>(ResultState.Loading)
    val userByEmail = _userByEmail.asStateFlow()

    private val _usersByEmails = MutableStateFlow<ResultState<List<Users>>>(ResultState.Loading)
    val usersByEmails = _usersByEmails.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _generateContent = MutableStateFlow<ResultState<Gemini>>(ResultState.Loading)
    val generateContent: StateFlow<ResultState<Gemini>> = _generateContent

    private val _quizCategories =
        MutableStateFlow<ResultState<List<QuizCategoryItem>>>(ResultState.Loading)
    val quizCategories = _quizCategories

    private val _quizQuestions =
        MutableStateFlow<ResultState<List<QuizQuestionsItem>>>(ResultState.Loading)
    val quizQuestions = _quizQuestions

    private val _notes =
        MutableStateFlow<ResultState<List<Notes>>>(ResultState.Loading)
    val notes = _notes

    private val _boards = MutableStateFlow<ResultState<List<Boards>>>(ResultState.Loading)
    val boards = _boards.asStateFlow()

    private val _papers = MutableStateFlow<ResultState<Papers>>(ResultState.Loading)
    val papers = _papers.asStateFlow()

    private val _subCategories =
        MutableStateFlow<ResultState<List<SubCategoriesItem>>>(ResultState.Loading)
    val subCategories = _subCategories.asStateFlow()

    private val _subQuestions =
        MutableStateFlow<ResultState<List<SubQuestionsItem>>>(ResultState.Loading)
    val subQuestions = _subQuestions.asStateFlow()

    private val _promotions = MutableStateFlow<ResultState<List<Promotions>>>(ResultState.Loading)
    val promotions = _promotions.asStateFlow()

    private val _allUsers = MutableStateFlow<ResultState<List<Users>>>(ResultState.Loading)
    val allUsers = _allUsers.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    private val _newMessages = MutableStateFlow<Set<String>>(emptySet())
    val newMessages: StateFlow<Set<String>> = _newMessages

    private val _communityMessages = MutableStateFlow<Map<String, List<CommunityMessage>>>(emptyMap())
    val communityMessages: StateFlow<Map<String, List<CommunityMessage>>> = _communityMessages

    private val _communities = MutableStateFlow<List<Community>>(emptyList())
    val communities: StateFlow<List<Community>> = _communities

    private val _userProfiles = MutableStateFlow<Map<String, UserProfile>>(emptyMap())
    val userProfiles: StateFlow<Map<String, UserProfile>> = _userProfiles

    private val _communityUsers = MutableStateFlow<List<Users>>(emptyList())
    val communityUsers: StateFlow<List<Users>> = _communityUsers

    private val _nonCommunityUsers = MutableStateFlow<List<Users>>(emptyList())
    val nonCommunityUsers: StateFlow<List<Users>> = _nonCommunityUsers

    init {
        fetchInitialData()
    }

    private fun fetchInitialData() {
        viewModelScope.launch {
            repository.getCommunities().collect { communityList ->
                _communities.value = communityList
            }
        }
        viewModelScope.launch {
            repository.getUserProfiles().collect { profiles ->
                _userProfiles.value = profiles.associateBy { it.email }
            }
        }
        viewModelScope.launch {
            repository.getMessages().collect { messageList ->
                _chatMessages.value = messageList
            }
        }
    }

    fun getUserProfile(email: String): UserProfile? {
        return _userProfiles.value[email]
    }

    fun observeCommunityMessages(communityId: String) {
        viewModelScope.launch {
            repository.getCommunityMessages(communityId).collect { messageList ->
                _communityMessages.value = _communityMessages.value.toMutableMap().apply {
                    put(communityId, messageList)
                }
            }
        }
    }

    fun getCommunity(communityId: String): Flow<Community?> = flow {
        val community = _communities.value.find { it.id == communityId }
        emit(community)
    }

    fun sendCommunityMessage(communityId: String, senderEmail: String, message: String) {
        viewModelScope.launch {
            repository.sendCommunityMessage(communityId, senderEmail, message)
        }
    }

    fun sendMessageChat(senderEmail: String, receiverEmail: String, message: String) {
        viewModelScope.launch {
            repository.sendMessagesBySocket(senderEmail, receiverEmail, message, null)
        }
    }

    fun updateNewMessages(newMessage: ChatMessage) {
        _newMessages.value += newMessage.senderEmail
    }

    fun observeChatMessages() {
        viewModelScope.launch {
            repository.getMessages().collect { messageList ->
                _chatMessages.value = messageList
            }
        }
    }

    fun getLatestMessageForUser(currentUserEmail: String, otherUserEmail: String): ChatMessage? {
        return _chatMessages.value
            .filter {
                (it.senderEmail == currentUserEmail && it.receiverEmail == otherUserEmail) ||
                        (it.senderEmail == otherUserEmail && it.receiverEmail == currentUserEmail)
            }
            .maxByOrNull { it.timestamp }
    }

    fun createCommunity(name: String, members: List<String>, admin: String) {
        viewModelScope.launch {
            repository.createCommunity(name, members, admin)
        }
    }

    fun getCommunityUsers(communityId: String): Flow<List<Users>> = flow {
        val communityUsersRef = repository.getCommunityUsers(communityId)
        communityUsersRef.collect { emails ->
            val users = emails.mapNotNull { email ->
                val userResult = getUserByEmailSync(email)
                if (userResult is ResultState.Success) {
                    userResult.data
                } else {
                    null
                }
            }
            emit(users)
        }
    }

    private suspend fun getUserByEmailSync(email: String): ResultState<Users> {
        return try {
            val response = repository.getUsersByEmails(listOf(email))
            ResultState.Success(response.firstOrNull() ?: return ResultState.Error("User not found"))
        } catch (e: Exception) {
            ResultState.Error(e.toString())
        }
    }

    fun fetchNonCommunityUsers(communityId: String) {
        viewModelScope.launch {
            val communityUsers = _communityUsers.value.map { it.email }
            val allUsers = repository.getUsersByEmails(emptyList()) // Assuming this fetches all users
            val nonCommunityUsers = allUsers.filter { it.email !in communityUsers }
            _nonCommunityUsers.value = nonCommunityUsers
        }
    }

    fun addUserToCommunity(communityId: String, userEmail: String) {
        viewModelScope.launch {
            repository.addUserToCommunity(communityId, userEmail)
            // Fetch updated community users after adding the user
            fetchCommunityUsers(communityId)
            // Fetch updated non-community users after adding the user
            fetchNonCommunityUsers(communityId)
        }
    }

    suspend fun removeUserFromCommunity(communityId: String, userEmail: String) {
        repository.removeUserFromCommunity(communityId, userEmail)
        fetchCommunityUsers(communityId)
        fetchNonCommunityUsers(communityId)
    }

    fun fetchCommunityUsers(communityId: String) {
        viewModelScope.launch {
            repository.getCommunityUsers(communityId).collect { emails ->
                val users = emails.mapNotNull { email ->
                    val userResult = getUserByEmailSync(email)
                    if (userResult is ResultState.Success) {
                        userResult.data
                    } else {
                        null
                    }
                }
                _communityUsers.value = users
                fetchNonCommunityUsers(communityId, users)
            }
        }
    }

    fun fetchNonCommunityUsers(communityId: String, communityUsers: List<Users>) {
        viewModelScope.launch {
            val communityUserEmails = communityUsers.map { it.email }
            val allUsers = repository.getAllUsers()
            val nonCommunityUsers = allUsers.filter { it.email !in communityUserEmails }
            _nonCommunityUsers.value = nonCommunityUsers
        }
    }

    fun uploadImageAndGetUrl(imageBytes: File, userId: String) {
        viewModelScope.launch {
            repository.uploadImageAndGetUrl(imageBytes, userId)
        }
    }
    fun uploadPdfAndGetUrl(pdfBytes: File, userId: String) {
        viewModelScope.launch {
            repository.uploadPdfAndGetUrl(pdfBytes, userId)
        }
    }

    fun sendImageMessage(senderEmail: String, receiverEmail: String, imageBytes: File) {
        viewModelScope.launch {
            repository.sendImageMessage(senderEmail, receiverEmail, imageBytes)
        }
    }
    fun sendPdfMessage(senderEmail: String, receiverEmail: String, pdfBytes: File) {
        viewModelScope.launch {
            repository.sendPdfMessage(senderEmail, receiverEmail, pdfBytes)
        }
    }

    fun sendCommunityImageMessage(communityId: String, senderEmail: String, imageBytes: File) {
        viewModelScope.launch {
            repository.sendCommunityImageMessage(communityId, senderEmail, imageBytes)
        }
    }
    fun sendCommunityPdfMessage(communityId: String, senderEmail: String, pdfBytes: File) {
        viewModelScope.launch {
            repository.sendCommunityPdfMessage(communityId, senderEmail, pdfBytes)
        }
    }


    fun getAllUsers() {
        viewModelScope.launch {
            _allUsers.value = ResultState.Loading
            try {
                val response = repository.getAllUsers()
                _allUsers.value = ResultState.Success(response)
            } catch (e: Exception) {
                _allUsers.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun getAllPromotions() {
        viewModelScope.launch {
            _promotions.value = ResultState.Loading
            try {
                val response = repository.getAllPromotions()
                _promotions.value = ResultState.Success(response)
            } catch (e: Exception) {
                _promotions.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun getAllSubCategories() {
        viewModelScope.launch {
            _subCategories.value = ResultState.Loading
            try {
                val response = repository.getAllSubCategories()
                _subCategories.value = ResultState.Success(response)
            } catch (e: Exception) {
                _subCategories.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun getAllSubQuestions() {
        viewModelScope.launch {
            _subQuestions.value = ResultState.Loading
            try {
                val response = repository.getSubQuestions()
                _subQuestions.value = ResultState.Success(response)
            } catch (e: Exception) {
                _subQuestions.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun getAllPapers(id: Long) {
        viewModelScope.launch {
            _papers.value = ResultState.Loading
            try {
                val response = repository.getAllPapersWithDetail(id = id)
                _papers.value = ResultState.Success(response)
            } catch (e: Exception) {
                _papers.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun getAllBoards() {
        viewModelScope.launch {
            _boards.value = ResultState.Loading
            try {
                val response = repository.getAllBoards()
                _boards.value = ResultState.Success(response)
            } catch (e: Exception) {
                _boards.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun getAllNotes() {
        viewModelScope.launch {
            _notes.value = ResultState.Loading
            try {
                val response = repository.getAllNotes()
                _notes.value = ResultState.Success(response)
            } catch (e: Exception) {
                _notes.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun getAllQuizQuestions() {
        viewModelScope.launch {
            _quizQuestions.value = ResultState.Loading
            try {
                val response = repository.getAllQuizzes()
                _quizQuestions.value = ResultState.Success(response)
            } catch (e: Exception) {
                _quizQuestions.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun getAllCategories() {
        viewModelScope.launch {
            _quizCategories.value = ResultState.Loading
            try {
                val response = repository.getAllCategories()
                _quizCategories.value = ResultState.Success(response)
            } catch (e: Exception) {
                _quizCategories.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun sendMessage(content: String) {
        val userMessage = Message(content, isUserMessage = true)
        viewModelScope.launch {
            databaseHelper.insertMessage(
                text = userMessage.text,
                isUserMessage = true,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
            val currentMessages = _messages.value.toMutableList()
            currentMessages.add(userMessage)
            currentMessages.add(Message("Loading...", isUserMessage = false, isLoading = true))
            _messages.value = currentMessages

            generateResponse(content)
        }
    }


    private fun generateResponse(content: String) {
        viewModelScope.launch {
            _generateContent.value = ResultState.Loading
            try {
                val response = repository.generateContent(content)
                _generateContent.value = ResultState.Success(response)

                val responseMessage =
                    response.candidates?.firstOrNull()?.content?.parts?.joinToString(" ") { it.text }
                        ?: "No response from server"

                databaseHelper.insertMessage(
                    text = responseMessage,
                    isUserMessage = false,
                    timestamp = Clock.System.now().toEpochMilliseconds()
                )

                val currentMessages = _messages.value.toMutableList()
                currentMessages.removeLast()
                currentMessages.add(Message(responseMessage, isUserMessage = false))
                _messages.value = currentMessages
            } catch (e: Exception) {
                _generateContent.value = ResultState.Error(e.toString())
                val errorMessage = "Error: ${e.message}"
                databaseHelper.insertMessage(
                    text = errorMessage,
                    isUserMessage = false,
                    timestamp = Clock.System.now().toEpochMilliseconds()
                )
                val currentMessages = _messages.value.toMutableList()
                currentMessages.removeLast()
                currentMessages.add(Message(errorMessage, isUserMessage = false))
                _messages.value = currentMessages
            }
        }
    }


    private fun convertDbMessageToUiMessage(dbMessage: org.mind.app.db.Message): Message {
        return Message(
            text = dbMessage.text,
            isUserMessage = dbMessage.isUserMessage == 1L,
            isLoading = false,
            showTypewriterEffect = false
        )
    }

    fun getUsersByEmails(
        email: List<String>,
    ) {
        viewModelScope.launch {
            _usersByEmails.value = ResultState.Loading
            try {
                val response = repository.getUsersByEmails(email)
                _usersByEmails.value = ResultState.Success(response)
            } catch (e: Exception) {
                _usersByEmails.value = ResultState.Error(e.toString())
            }
        }
    }

    fun getUserByEmail(
        email: String,
    ) {
        viewModelScope.launch {
            _userByEmail.value = ResultState.Loading
            try {
                val response = repository.getUserByEmail(email)
                _userByEmail.value = ResultState.Success(response)
            } catch (e: Exception) {
                _userByEmail.value = ResultState.Error(e.toString())
            }
        }
    }

    fun loginServer(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _loginServer.value = ResultState.Loading
            try {
                val response = repository.loginServerUser(email, password)
                _loginServer.value = ResultState.Success(response)
            } catch (e: Exception) {
                _loginServer.value = ResultState.Error(e.toString())
            }
        }
    }

    fun updateUserDetails(
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
        viewModelScope.launch {
            _updateUserDetails.value = ResultState.Loading
            try {
                repository.updateUserDetails(
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
                _updateUserDetails.value = ResultState.Success("Updated Successfully")
            } catch (e: Exception) {
                _updateUserDetails.value = ResultState.Error(e.toString())
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginUser.value = ResultState.Loading
            try {
                repository.authenticate(email, password)
                _loginUser.value = ResultState.Success("Success")
            } catch (e: Exception) {
                _loginUser.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun getUserDetail(userId: Int) {
        viewModelScope.launch {
            _userDetail.value = ResultState.Loading
            try {
                val response = repository.getUsersById(userId)
                _userDetail.value = ResultState.Success(response)
            } catch (e: Exception) {
                _userDetail.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun signUpUserServer(
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
    ) {
        viewModelScope.launch {
            _signupUsersServer.value = ResultState.Loading
            try {
                val response = repository.signUpUser(
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
                _signupUsersServer.value = ResultState.Success(response)
            } catch (e: Exception) {
                _signupUsersServer.value = ResultState.Error(e.message.toString())
            }
        }
    }


    fun createAccount(email: String, password: String) {
        viewModelScope.launch {
            _loginUser.value = ResultState.Loading
            try {
                repository.createUser(email, password)
                _createUser.value = ResultState.Success("Success")
            } catch (e: Exception) {
                _createUser.value = ResultState.Success(e.message.toString())
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPasswordState.value = ResultState.Loading
            try {
                repository.resetPassword(email)
                _resetPasswordState.value = ResultState.Success("Password reset email sent.")
            } catch (e: Exception) {
                _resetPasswordState.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _signOutState.value = ResultState.Loading
            try {
                repository.signOut()
                _signOutState.value = ResultState.Success("Signed out successfully.")
            } catch (e: Exception) {
                _signOutState.value = ResultState.Error(e.message.toString())
            }
        }
    }

}