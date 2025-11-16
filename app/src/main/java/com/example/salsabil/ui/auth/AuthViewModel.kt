package com.example.salsabil.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salsabil.data.local.entities.User
import com.example.salsabil.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    private val _registerResult = MutableLiveData<Result<Long>>()
    val registerResult: LiveData<Result<Long>> = _registerResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.loginUser(email, password)
            _loginResult.postValue(result)
        }
    }

    fun register(username: String, email: String, password: String, dateOfBirth: Long) {
        viewModelScope.launch {
            val result = userRepository.registerUser(username, email, password, dateOfBirth)
            _registerResult.postValue(result)
        }
    }
}
