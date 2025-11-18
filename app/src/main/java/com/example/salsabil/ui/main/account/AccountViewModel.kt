package com.example.salsabil.ui.main.account


import androidx.lifecycle.*
import com.example.salsabil.data.local.entities.User
import com.example.salsabil.data.repository.UserRepository
import kotlinx.coroutines.launch

class AccountViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadUser(userId: Long) {
        viewModelScope.launch {
            try {
                // Utiliser la méthode du repository qui accède au DAO
                val userData = userRepository.userDao.getUserByIdSync(userId)
                _user.postValue(userData)
            } catch (e: Exception) {
                _error.postValue("Error loading user: ${e.message}")
            }
        }
    }

    fun updateNotificationSettings(userId: Long, enabled: Boolean) {
        viewModelScope.launch {
            try {
                userRepository.updateNotificationSettings(userId, enabled)
            } catch (e: Exception) {
                _error.postValue("Error updating notifications: ${e.message}")
            }
        }
    }

    fun updateReminderInterval(userId: Long, intervalMinutes: Int) {
        viewModelScope.launch {
            try {
                userRepository.updateReminderInterval(userId, intervalMinutes)
            } catch (e: Exception) {
                _error.postValue("Error updating interval: ${e.message}")
            }
        }
    }
}