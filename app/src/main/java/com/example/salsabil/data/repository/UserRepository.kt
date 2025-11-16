package com.example.salsabil.data.repository


import com.example.salsabil.data.local.dao.UserDao
import com.example.salsabil.data.local.entities.User
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(
        username: String,
        email: String,
        password: String,
        dateOfBirth: Long
    ): Result<Long> {
        return try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                return Result.failure(Exception("Email already registered"))
            }

            val user = User(
                username = username,
                email = email,
                passwordHash = hashPassword(password),
                dateOfBirth = dateOfBirth
            )

            val userId = userDao.insertUser(user)
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByEmail(email)
                ?: return Result.failure(Exception("User not found"))

            if (user.passwordHash != hashPassword(password)) {
                return Result.failure(Exception("Invalid password"))
            }

            userDao.updateLastLogin(user.userId, System.currentTimeMillis())
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserById(userId: Long): Flow<User?> = userDao.getUserById(userId)

    suspend fun updateDailyGoal(userId: Long, goalMl: Int) {
        userDao.updateDailyGoal(userId, goalMl)
    }

    suspend fun updateNotificationSettings(userId: Long, enabled: Boolean) {
        userDao.updateNotificationSettings(userId, enabled)
    }

    suspend fun updateReminderInterval(userId: Long, intervalMinutes: Int) {
        userDao.updateReminderInterval(userId, intervalMinutes)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(password.toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
