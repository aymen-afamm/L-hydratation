package com.example.salsabil.ui.main.account


import androidx.lifecycle.*
import com.example.salsabil.data.local.entities.User
import com.example.salsabil.data.repository.UserRepository
import kotlinx.coroutines.launch

class AccountViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUserId = MutableLiveData<Long>()

    val user: LiveData<User?> = _currentUserId.switchMap { userId ->
        userRepository.getUserById(userId).asLiveData()
    }

    fun setUserId(userId: Long) {
        _currentUserId.value = userId
    }

    fun updateNotificationSettings(enabled: Boolean) {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                userRepository.updateNotificationSettings(userId, enabled)
            }
        }
    }

    fun updateReminderInterval(intervalMinutes: Int) {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                userRepository.updateReminderInterval(userId, intervalMinutes)
            }
        }
    }
}
