package org.mind.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mind.app.domain.model.user.User
import org.mind.app.domain.model.users.Users
import org.mind.app.domain.repository.Repository
import org.mind.app.domain.usecases.ResultState

class MainViewModel(private val repository: Repository) : ViewModel() {
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
    fun signUpUserServer(email: String,password: String) {
        viewModelScope.launch {
            _signupUsersServer.value = ResultState.Loading
            try {
               val response =  repository.signUpUser(email,password)
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