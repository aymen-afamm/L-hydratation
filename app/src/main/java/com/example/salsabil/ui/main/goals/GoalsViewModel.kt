package com.example.salsabil.ui.main.goals


import androidx.lifecycle.*
import com.example.salsabil.data.local.entities.Goal
import com.example.salsabil.data.repository.GoalRepository
import com.example.salsabil.data.repository.UserRepository
import com.example.salsabil.utils.DateUtils
import kotlinx.coroutines.launch

class GoalsViewModel(
    private val goalRepository: GoalRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUserId = MutableLiveData<Long>()

    val currentGoal: LiveData<Goal?> = _currentUserId.switchMap { userId ->
        val date = DateUtils.normalizeToStartOfDay(System.currentTimeMillis())
        goalRepository.getGoalForDate(userId, date).asLiveData()
    }

    val goalsHistory: LiveData<List<Goal>> = _currentUserId.switchMap { userId ->
        goalRepository.getRecentGoals(userId, 30).asLiveData()
    }

    fun setUserId(userId: Long) {
        _currentUserId.value = userId
    }

    fun setDailyGoal(targetMl: Int) {
        viewModelScope.launch {
            val userId = _currentUserId.value ?: return@launch
            val date = DateUtils.normalizeToStartOfDay(System.currentTimeMillis())
            goalRepository.createOrUpdateGoalForToday(userId, targetMl, date)
            userRepository.updateDailyGoal(userId, targetMl)
        }
    }
}
