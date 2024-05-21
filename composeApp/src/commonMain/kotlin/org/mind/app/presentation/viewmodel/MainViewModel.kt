package org.mind.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mind.app.domain.repository.Repository
import org.mind.app.domain.usecases.ResultState

class MainViewModel(private val repository: Repository) : ViewModel() {
    val _createUser = MutableStateFlow<ResultState<String>>(ResultState.Loading)
    var createUser: StateFlow<ResultState<String>> = _createUser.asStateFlow()

    fun createUser(email: String, password: String) {
        viewModelScope.launch {
            _createUser.value = ResultState.Loading
            try {
                val firebaseAuth = repository.createNewUser(email, password)
                if (firebaseAuth.user == null) {
                    _createUser.value = ResultState.Success("Success")
                } else {
                    _createUser.value = ResultState.Error("Error")
                }
            } catch (e: Exception) {
                _createUser.value = ResultState.Error(e.message.toString())
            }
        }
    }

}