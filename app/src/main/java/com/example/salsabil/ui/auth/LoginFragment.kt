package com.example.salsabil.ui.auth


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.example.salsabil.R
import com.example.salsabil.data.local.database.SalsabilDatabase
import com.example.salsabil.data.preferences.PreferencesManager
import com.example.salsabil.data.repository.UserRepository
import com.example.salsabil.ui.main.MainActivity
import com.example.salsabil.utils.showToast
import com.example.salsabil.workers.ReminderScheduler

class LoginFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var preferencesManager: PreferencesManager

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesManager = PreferencesManager(requireContext())

        val database = SalsabilDatabase.getDatabase(requireContext())
        val repository = UserRepository(database.userDao())
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        progressBar = view.findViewById(R.id.progressBar)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            progressBar.visibility = View.GONE
            btnLogin.isEnabled = true

            result.onSuccess { user ->
                preferencesManager.currentUserId = user.userId
                preferencesManager.isLoggedIn = true

                // Schedule reminders
                ReminderScheduler.scheduleReminders(
                    requireContext(),
                    user.userId,
                    user.reminderIntervalMinutes
                )

                showToast(getString(R.string.login_success))

                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

            result.onFailure { error ->
                showToast(error.message ?: getString(R.string.login_failed))
            }
        }
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                progressBar.visibility = View.VISIBLE
                btnLogin.isEnabled = false
                viewModel.login(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
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
}
