package com.example.salsabil.utils


import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.salsabil.data.local.entities.User
import com.example.salsabil.data.repository.UserRepository

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().showToast(message, duration)
}

fun Int.toFormattedAmount(): String {
    return when {
        this >= 1000 -> "${this / 1000f}L"
        else -> "${this}ml"
    }
}

fun Long.toFormattedDate(): String {
    return DateUtils.formatDate(this)
}

fun Long.toFormattedTime(): String {
    return DateUtils.formatTime(this)
}

// ============================================
// Extension Functions (Optional - in utils/)
// ============================================
private suspend fun UserRepository.getUserByEmail(email: String): User? {
    return try {
        // Assuming you have this method in your repository
        val dao = this.javaClass.getDeclaredField("userDao").apply { isAccessible = true }.get(this) as com.example.salsabil.data.local.dao.UserDao
        dao.getUserByEmail(email)
    } catch (e: Exception) {
        null
    }
}

private suspend fun UserRepository.insertUser(user: User): Long {
    return try {
        val dao = this.javaClass.getDeclaredField("userDao").apply { isAccessible = true }.get(this) as com.example.salsabil.data.local.dao.UserDao
        dao.insertUser(user)
    } catch (e: Exception) {
        -1L
    }
}

private suspend fun UserRepository.updateLastLogin(userId: Long, timestamp: Long) {
    try {
        val dao = this.javaClass.getDeclaredField("userDao").apply { isAccessible = true }.get(this) as com.example.salsabil.data.local.dao.UserDao
        dao.updateLastLogin(userId, timestamp)
    } catch (e: Exception) {
        // Handle error
    }
}