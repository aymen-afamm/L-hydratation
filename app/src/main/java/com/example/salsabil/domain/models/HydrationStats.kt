package com.example.salsabil.domain.models


data class HydrationStats(
    val totalConsumedToday: Int,
    val dailyGoal: Int,
    val percentageAchieved: Int,
    val remainingAmount: Int,
    val weeklyData: List<DayData>
)

data class DayData(
    val dayName: String,
    val amount: Int,
    val date: Long
)
