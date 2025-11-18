package com.example.salsabil.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.example.salsabil.R
import com.example.salsabil.data.local.database.SalsabilDatabase
import com.example.salsabil.data.local.entities.User
import com.example.salsabil.data.preferences.PreferencesManager
import com.example.salsabil.data.repository.UserRepository
import com.example.salsabil.ui.main.MainActivity
import com.example.salsabil.workers.ReminderScheduler
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.Calendar


class AuthActivity : AppCompatActivity() {

    // Login Views
    private lateinit var etLoginEmail: TextInputEditText
    private lateinit var etLoginPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnGoToRegister: MaterialButton

    // Register Views
    private lateinit var etRegisterUsername: TextInputEditText
    private lateinit var etRegisterEmail: TextInputEditText
    private lateinit var etRegisterPassword: TextInputEditText
    private lateinit var etRegisterConfirmPassword: TextInputEditText
    private lateinit var etRegisterDateOfBirth: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var btnGoToLogin: MaterialButton

    // Containers
    private lateinit var loginContainer: View
    private lateinit var registerContainer: View

    // Repository & Preferences
    private lateinit var userRepository: UserRepository
    private lateinit var preferencesManager: PreferencesManager

    private var selectedDateOfBirth: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_unified)

        // Initialize Repository
        val database = SalsabilDatabase.getDatabase(this)
        userRepository = UserRepository(database.userDao())
        preferencesManager = PreferencesManager(this)

        // Initialize Views
        initViews()

        // Setup Listeners
        setupListeners()

        // Show Login by default
        showLogin()
    }

    private fun initViews() {
        // Containers
        loginContainer = findViewById(R.id.loginContainer)
        registerContainer = findViewById(R.id.registerContainer)

        // Login Views
        etLoginEmail = findViewById(R.id.etLoginEmail)
        etLoginPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById(R.id.btnGoToRegister)

        // Register Views
        etRegisterUsername = findViewById(R.id.etRegisterUsername)
        etRegisterEmail = findViewById(R.id.etRegisterEmail)
        etRegisterPassword = findViewById(R.id.etRegisterPassword)
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword)
        etRegisterDateOfBirth = findViewById(R.id.etRegisterDateOfBirth)
        btnRegister = findViewById(R.id.btnRegister)
        btnGoToLogin = findViewById(R.id.btnGoToLogin)
    }

    private fun setupListeners() {
        // Login Button
        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val password = etLoginPassword.text.toString().trim()

            if (validateLoginInput(email, password)) {
                performLogin(email, password)
            }
        }

        // Go to Register
        btnGoToRegister.setOnClickListener {
            showRegister()
        }

        // Register Button
        btnRegister.setOnClickListener {
            val username = etRegisterUsername.text.toString().trim()
            val email = etRegisterEmail.text.toString().trim()
            val password = etRegisterPassword.text.toString().trim()
            val confirmPassword = etRegisterConfirmPassword.text.toString().trim()

            if (validateRegisterInput(username, email, password, confirmPassword)) {
                performRegister(username, email, password)
            }
        }

        // Go to Login
        btnGoToLogin.setOnClickListener {
            showLogin()
        }

        // Date Picker
        etRegisterDateOfBirth.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showLogin() {
        loginContainer.visibility = View.VISIBLE
        registerContainer.visibility = View.GONE
        clearLoginFields()
    }

    private fun showRegister() {
        loginContainer.visibility = View.GONE
        registerContainer.visibility = View.VISIBLE
        clearRegisterFields()
    }

    // ============================================
    // LOGIN LOGIC
    // ============================================

    private fun validateLoginInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            showToast(getString(R.string.error_email_empty))
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(getString(R.string.error_email_invalid))
            return false
        }

        if (password.isEmpty()) {
            showToast(getString(R.string.error_password_empty))
            return false
        }

        if (password.length < 6) {
            showToast(getString(R.string.error_password_short))
            return false
        }

        return true
    }

    private fun performLogin(email: String, password: String) {
        btnLogin.isEnabled = false
        btnLogin.text = getString(R.string.loading)

        lifecycleScope.launch {
            try {
                val user = userRepository.userDao.getUserByEmail(email)

                if (user == null) {
                    showToast(getString(R.string.login_failed))
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.login)
                    return@launch
                }

                if (user.passwordHash != hashPassword(password)) {
                    showToast(getString(R.string.error_password_invalid))
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.login)
                    return@launch
                }

                // Update last login
//                userRepository.updateLastLogin(user.userId, System.currentTimeMillis())

                // Save session
                preferencesManager.currentUserId = user.userId
                preferencesManager.isLoggedIn = true

                // Schedule reminders
                ReminderScheduler.scheduleReminders(
                    this@AuthActivity,
                    user.userId,
                    user.reminderIntervalMinutes
                )

                showToast(getString(R.string.login_success))

                // Navigate to MainActivity
                val intent = Intent(this@AuthActivity, MainActivity::class.java)
                intent.putExtra("USER_ID", user.userId)
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                showToast(getString(R.string.login_failed))
                btnLogin.isEnabled = true
                btnLogin.text = getString(R.string.login)
            }
        }
    }

    // ============================================
    // REGISTER LOGIC
    // ============================================

    private fun validateRegisterInput(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (username.isEmpty()) {
            showToast(getString(R.string.error_username_empty))
            return false
        }

        if (username.length < 3) {
            showToast(getString(R.string.error_username_short))
            return false
        }

        if (email.isEmpty()) {
            showToast(getString(R.string.error_email_empty))
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(getString(R.string.error_email_invalid))
            return false
        }

        if (password.isEmpty()) {
            showToast(getString(R.string.error_password_empty))
            return false
        }

        if (password.length < 6) {
            showToast(getString(R.string.error_password_short))
            return false
        }

        if (password != confirmPassword) {
            showToast(getString(R.string.error_password_mismatch))
            return false
        }

        if (selectedDateOfBirth == 0L) {
            showToast(getString(R.string.error_dob_empty))
            return false
        }

        if (!isAgeValid(selectedDateOfBirth)) {
            showToast("You must be at least 18 years old")
            return false
        }

        return true
    }

    private fun performRegister(username: String, email: String, password: String) {
        btnRegister.isEnabled = false
        btnRegister.text = getString(R.string.loading)

        lifecycleScope.launch {
            try {
                // Check if email exists
                val existingUser = userRepository.userDao.getUserByEmail(email)
                if (existingUser != null) {
                    showToast("Email already registered")
                    btnRegister.isEnabled = true
                    btnRegister.text = getString(R.string.register)
                    return@launch
                }

                // Create new user
                val user = User(
                    username = username,
                    email = email,
                    passwordHash = hashPassword(password),
                    dateOfBirth = selectedDateOfBirth
                )

                val userId = userRepository.userDao.insertUser(user)

                showToast(getString(R.string.register_success))

                // Auto login
                preferencesManager.currentUserId = userId
                preferencesManager.isLoggedIn = true

                // Schedule reminders
                ReminderScheduler.scheduleReminders(
                    this@AuthActivity,
                    userId,
                    120
                )

                // Navigate to MainActivity
                val intent = Intent(this@AuthActivity, MainActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                showToast(getString(R.string.register_failed))
                btnRegister.isEnabled = true
                btnRegister.text = getString(R.string.register)
            }
        }
    }

    // ============================================
    // DATE PICKER
    // ============================================

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -18)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedDateOfBirth = selectedCalendar.timeInMillis

                etRegisterDateOfBirth.setText("$dayOfMonth/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    // ============================================
    // HELPER FUNCTIONS
    // ============================================

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(password.toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun isAgeValid(birthTimestamp: Long): Boolean {
        val birthCalendar = Calendar.getInstance()
        birthCalendar.timeInMillis = birthTimestamp

        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age >= 18
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun clearLoginFields() {
        etLoginEmail.text?.clear()
        etLoginPassword.text?.clear()
    }

    private fun clearRegisterFields() {
        etRegisterUsername.text?.clear()
        etRegisterEmail.text?.clear()
        etRegisterPassword.text?.clear()
        etRegisterConfirmPassword.text?.clear()
        etRegisterDateOfBirth.text?.clear()
        selectedDateOfBirth = 0
    }
}