package com.example.salsabil.domain.models


data class UserProfile(
    val userId: Long,
    val username: String,
    val email: String,
    val age: Int,
    val dailyGoalMl: Int,
    val notificationsEnabled: Boolean,
    val reminderIntervalMinutes: Int
)