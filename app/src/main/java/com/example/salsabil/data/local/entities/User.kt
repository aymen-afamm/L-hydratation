package com.example.salsabil.data.local.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val dateOfBirth: Long,
    val dailyGoalMl: Int = 2000,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val notificationsEnabled: Boolean = true,
    val reminderIntervalMinutes: Int = 120
)
