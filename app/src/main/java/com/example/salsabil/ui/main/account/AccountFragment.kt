package com.example.salsabil.ui.main.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import com.example.salsabil.R
import com.example.salsabil.data.local.database.SalsabilDatabase
import com.example.salsabil.data.preferences.PreferencesManager
import com.example.salsabil.ui.auth.AuthActivity
import com.example.salsabil.utils.showToast
import com.example.salsabil.workers.ReminderScheduler
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var database: SalsabilDatabase
    private var userId: Long = -1

    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvDailyGoal: TextView
    private lateinit var switchNotifications: SwitchCompat
    private lateinit var sliderInterval: Slider
    private lateinit var tvIntervalValue: TextView
    private lateinit var btnLogout: Button

    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: Long): AccountFragment {
            return AccountFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_USER_ID, userId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getLong(ARG_USER_ID) ?: -1
        preferencesManager = PreferencesManager(requireContext())
        database = SalsabilDatabase.getDatabase(requireContext())

        // Si userId est -1, récupérer depuis les préférences
        if (userId == -1L) {
            userId = preferencesManager.currentUserId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialiser les vues
        initViews(view)

        // Charger les données utilisateur
        loadUserData()

        // Setup listeners
        setupListeners()
    }

    private fun initViews(view: View) {
        tvUsername = view.findViewById(R.id.tvUsername)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvDailyGoal = view.findViewById(R.id.tvDailyGoal)
        switchNotifications = view.findViewById(R.id.switchNotifications)
        sliderInterval = view.findViewById(R.id.sliderReminderInterval)
        tvIntervalValue = view.findViewById(R.id.tvIntervalValue)
        btnLogout = view.findViewById(R.id.btnLogout)
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            try {
                val user = database.userDao().getUserByIdSync(userId)

                if (user != null) {
                    // Afficher les informations
                    tvUsername.text = user.username
                    tvEmail.text = user.email
                    tvDailyGoal.text = getString(R.string.daily_goal_display, user.dailyGoalMl)

                    // Paramètres
                    switchNotifications.isChecked = user.notificationsEnabled
                    sliderInterval.value = user.reminderIntervalMinutes.toFloat()
                    updateIntervalText(user.reminderIntervalMinutes)
                } else {
                    // Utilisateur introuvable, déconnecter
                    showToast("User not found. Please login again.")
                    logout()
                }
            } catch (e: Exception) {
                showToast("Error loading user data: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun setupListeners() {
        // Switch notifications
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            updateNotificationSettings(isChecked)
        }

        // Slider interval
        sliderInterval.addOnChangeListener { _, value, _ ->
            val interval = value.toInt()
            updateIntervalText(interval)
        }

        sliderInterval.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                val interval = slider.value.toInt()
                updateReminderInterval(interval)
            }
        })

        // Bouton logout
        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun updateNotificationSettings(enabled: Boolean) {
        lifecycleScope.launch {
            try {
                database.userDao().updateNotificationSettings(userId, enabled)

                if (enabled) {
                    val interval = sliderInterval.value.toInt()
                    ReminderScheduler.scheduleReminders(requireContext(), userId, interval)
                    showToast(getString(R.string.notifications_enabled))
                } else {
                    ReminderScheduler.cancelReminders(requireContext())
                    showToast(getString(R.string.notifications_disabled))
                }
            } catch (e: Exception) {
                showToast("Error updating notifications: ${e.message}")
            }
        }
    }

    private fun updateReminderInterval(intervalMinutes: Int) {
        lifecycleScope.launch {
            try {
                database.userDao().updateReminderInterval(userId, intervalMinutes)

                if (switchNotifications.isChecked) {
                    ReminderScheduler.updateReminderInterval(requireContext(), userId, intervalMinutes)
                }

                showToast(getString(R.string.reminder_interval_updated))
            } catch (e: Exception) {
                showToast("Error updating interval: ${e.message}")
            }
        }
    }

    private fun updateIntervalText(minutes: Int) {
        val hours = minutes / 60
        val mins = minutes % 60

        tvIntervalValue.text = when {
            hours > 0 && mins > 0 -> getString(R.string.interval_hours_mins, hours, mins)
            hours > 0 -> getString(R.string.interval_hours, hours)
            else -> getString(R.string.interval_mins, mins)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.logout_title)
            .setMessage(R.string.logout_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                logout()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun logout() {
        // Annuler les rappels
        ReminderScheduler.cancelReminders(requireContext())

        // Effacer la session
        preferencesManager.clearSession()

        // Rediriger vers AuthActivity
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
