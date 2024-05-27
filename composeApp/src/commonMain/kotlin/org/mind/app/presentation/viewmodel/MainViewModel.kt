package org.mind.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.mind.app.data.local.DatabaseHelper
import org.mind.app.domain.model.category.QuizCategoryItem
import org.mind.app.domain.model.gemini.Gemini
import org.mind.app.domain.model.message.Message
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

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _generateContent = MutableStateFlow<ResultState<Gemini>>(ResultState.Loading)
    val generateContent: StateFlow<ResultState<Gemini>> = _generateContent

    private val _quizCategories =
        MutableStateFlow<ResultState<List<QuizCategoryItem>>>(ResultState.Loading)
    val quizCategories = _quizCategories

    init {
        viewModelScope.launch {
            databaseHelper.getAllMessages().collect { localMessages ->
                val convertedMessages = localMessages.map { convertDbMessageToUiMessage(it) }
                _messages.value = convertedMessages
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