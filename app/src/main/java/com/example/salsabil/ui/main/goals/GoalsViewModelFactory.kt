package com.example.salsabil.ui.main.goals


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.salsabil.data.repository.GoalRepository
import com.example.salsabil.data.repository.UserRepository

class GoalsViewModelFactory(
    private val goalRepository: GoalRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalsViewModel::class.java)) {
            return GoalsViewModel(goalRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}