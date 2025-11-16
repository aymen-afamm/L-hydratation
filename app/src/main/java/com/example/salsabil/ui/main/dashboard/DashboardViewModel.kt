package com.example.salsabil.ui.main.dashboard


import androidx.lifecycle.*
import kotlinx.coroutines.flow.map

import com.example.salsabil.data.local.entities.Goal
import com.example.salsabil.data.repository.ConsumptionRepository
import com.example.salsabil.data.repository.GoalRepository
import com.example.salsabil.data.repository.UserRepository
import com.example.salsabil.domain.models.DayData
import com.example.salsabil.domain.usecases.CalculateDailyProgressUseCase
import com.example.salsabil.domain.usecases.GetWeeklyStatsUseCase
import com.example.salsabil.utils.DateUtils
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val consumptionRepository: ConsumptionRepository,
    private val goalRepository: GoalRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val calculateProgressUseCase = CalculateDailyProgressUseCase()
    private val weeklyStatsUseCase = GetWeeklyStatsUseCase()

    private val _currentUserId = MutableLiveData<Long>()

    val todayConsumption: LiveData<Int> = _currentUserId.switchMap { userId ->
        val (start, end) = DateUtils.getTodayRange()
        consumptionRepository.getTotalForDayFlow(userId, start, end)
            .map { it ?: 0 }
            .asLiveData()
    }

    val todayGoal: LiveData<Goal?> = _currentUserId.switchMap { userId ->
        val date = DateUtils.normalizeToStartOfDay(System.currentTimeMillis())
        goalRepository.getGoalForDate(userId, date).asLiveData()
    }

    val progressPercentage: LiveData<Int> = MediatorLiveData<Int>().apply {
        addSource(todayConsumption) { consumed ->
            val goal = todayGoal.value?.targetMl ?: 2000
            value = calculateProgressUseCase.execute(consumed, goal)
        }
        addSource(todayGoal) { goal ->
            val consumed = todayConsumption.value ?: 0
            value = calculateProgressUseCase.execute(consumed, goal?.targetMl ?: 2000)
        }
    }

    private val _weeklyStats = MutableLiveData<List<DayData>>()
    val weeklyStats: LiveData<List<DayData>> = _weeklyStats

    fun setUserId(userId: Long) {
        _currentUserId.value = userId
        loadWeeklyStats()
    }

    fun addConsumption(amountMl: Int) {
        viewModelScope.launch {
            consumptionRepository.logWaterIntake(
                userId = _currentUserId.value ?: return@launch,
                amountMl = amountMl
            )

            // Update goal progress
            updateGoalProgress()
            loadWeeklyStats()
        }
    }

    private fun loadWeeklyStats() {
        viewModelScope.launch {
            val userId = _currentUserId.value ?: return@launch
            val (start, end) = DateUtils.getWeekRange()
            val logs = consumptionRepository.getAllLogsForUser(userId).asLiveData().value ?: emptyList()
            val weekData = weeklyStatsUseCase.execute(logs)
            _weeklyStats.postValue(weekData)
        }
    }

    private suspend fun updateGoalProgress() {
        val userId = _currentUserId.value ?: return
        val date = DateUtils.normalizeToStartOfDay(System.currentTimeMillis())
        val goal = goalRepository.getGoalForDate(userId, date).asLiveData().value

        if (goal != null) {
            val (start, end) = DateUtils.getTodayRange()
            val consumed = consumptionRepository.getTotalForDay(userId, start, end)
            goalRepository.updateGoalProgress(goal.goalId, consumed, goal.targetMl)
        }
    }
}
