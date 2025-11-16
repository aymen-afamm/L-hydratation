package com.example.salsabil.ui.main.account


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.salsabil.data.repository.UserRepository

class AccountViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}