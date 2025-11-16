package com.example.salsabil.ui.main.dashboard


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.salsabil.data.repository.ConsumptionRepository
import com.example.salsabil.data.repository.GoalRepository
import com.example.salsabil.data.repository.UserRepository

class DashboardViewModelFactory(
    private val consumptionRepository: ConsumptionRepository,
    private val goalRepository: GoalRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(consumptionRepository, goalRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}