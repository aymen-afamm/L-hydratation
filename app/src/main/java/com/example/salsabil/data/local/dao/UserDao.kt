package com.example.salsabil.data.local.dao


import androidx.room.*
import com.example.salsabil.data.local.entities.User

import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: Long): Flow<User?>

    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE userId = :userId")
    suspend fun updateLastLogin(userId: Long, timestamp: Long)

    @Query("UPDATE users SET dailyGoalMl = :goalMl WHERE userId = :userId")
    suspend fun updateDailyGoal(userId: Long, goalMl: Int)

    @Query("UPDATE users SET notificationsEnabled = :enabled WHERE userId = :userId")
    suspend fun updateNotificationSettings(userId: Long, enabled: Boolean)

    @Query("UPDATE users SET reminderIntervalMinutes = :intervalMinutes WHERE userId = :userId")
    suspend fun updateReminderInterval(userId: Long, intervalMinutes: Int)

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserByIdSync(userId: Long): User?
}
