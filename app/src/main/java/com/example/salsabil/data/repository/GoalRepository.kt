package com.example.salsabil.data.repository


import com.example.salsabil.data.local.dao.GoalDao
import com.example.salsabil.data.local.entities.Goal
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val goalDao: GoalDao) {

    suspend fun createOrUpdateGoalForToday(userId: Long, targetMl: Int, date: Long): Long {
        val existingGoal = goalDao.getGoalForDate(userId, date)

        return if (existingGoal != null) {
            val updatedGoal = existingGoal.copy(targetMl = targetMl)
            goalDao.updateGoal(updatedGoal)
            existingGoal.goalId
        } else {
            val newGoal = Goal(
                userId = userId,
                date = date,
                targetMl = targetMl
            )
            goalDao.insertGoal(newGoal)
        }
    }

    suspend fun updateGoalProgress(goalId: Long, achievedMl: Int, targetMl: Int) {
        val isAchieved = achievedMl >= targetMl
        if (isAchieved) {
            goalDao.markGoalAsAchieved(goalId, achievedMl, true, System.currentTimeMillis())
        } else {
            goalDao.updateGoalProgress(goalId, achievedMl, false)
        }
    }

    fun getGoalForDate(userId: Long, date: Long): Flow<Goal?> {
        return goalDao.getGoalForDateFlow(userId, date)
    }

    fun getRecentGoals(userId: Long, limit: Int = 30): Flow<List<Goal>> {
        return goalDao.getRecentGoals(userId, limit)
    }

    suspend fun getGoalsInRange(userId: Long, startDate: Long, endDate: Long): List<Goal> {
        return goalDao.getGoalsInRange(userId, startDate, endDate)
    }

    fun getAchievedGoalsCount(userId: Long): Flow<Int> {
        return goalDao.getAchievedGoalsCount(userId)
    }
}