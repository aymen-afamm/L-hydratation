package com.example.salsabil.domain.usecases


class CalculateDailyProgressUseCase {

    fun execute(consumed: Int, goal: Int): Int {
        if (goal <= 0) return 0
        return ((consumed.toFloat() / goal.toFloat()) * 100).toInt().coerceIn(0, 100)
    }

    fun getRemainingAmount(consumed: Int, goal: Int): Int {
        return (goal - consumed).coerceAtLeast(0)
    }

    fun isGoalAchieved(consumed: Int, goal: Int): Boolean {
        return consumed >= goal
    }
}
