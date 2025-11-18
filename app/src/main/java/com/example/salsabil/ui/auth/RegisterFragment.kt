//package com.example.salsabil.ui.auth
//
//
//import android.app.DatePickerDialog
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.ProgressBar
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import com.google.android.material.textfield.TextInputEditText
//import com.example.salsabil.R
//import com.example.salsabil.data.local.database.SalsabilDatabase
//import com.example.salsabil.data.preferences.PreferencesManager
//import com.example.salsabil.data.repository.UserRepository
//import com.example.salsabil.domain.usecases.ValidateAgeUseCase
//import com.example.salsabil.ui.main.MainActivity
//import com.example.salsabil.utils.showToast
//import com.example.salsabil.workers.ReminderScheduler
//import java.util.Calendar
//
//class RegisterFragment : Fragment() {
//
//    private lateinit var viewModel: AuthViewModel
//    private lateinit var preferencesManager: PreferencesManager
//    private val validateAgeUseCase = ValidateAgeUseCase()
//
//    private lateinit var etUsername: TextInputEditText
//    private lateinit var etEmail: TextInputEditText
//    private lateinit var etPassword: TextInputEditText
//    private lateinit var etConfirmPassword: TextInputEditText
//    private lateinit var etDateOfBirth: TextInputEditText
//    private lateinit var btnRegister: Button
//    private lateinit var progressBar: ProgressBar
//
//    private var selectedDateOfBirth: Long = 0
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_register, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        preferencesManager = PreferencesManager(requireContext())
//
//        val database = SalsabilDatabase.getDatabase(requireContext())
//        val repository = UserRepository(database.userDao())
//        val factory = AuthViewModelFactory(repository)
//        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
//
//        etUsername = view.findViewById(R.id.etUsername)
//        etEmail = view.findViewById(R.id.etEmail)
//        etPassword = view.findViewById(R.id.etPassword)
//        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
//        etDateOfBirth = view.findViewById(R.id.etDateOfBirth)
//        btnRegister = view.findViewById(R.id.btnRegister)
//        progressBar = view.findViewById(R.id.progressBar)
//
//        setupObservers()
//        setupListeners()
//    }
//
//    private fun setupObservers() {
//        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
//            progressBar.visibility = View.GONE
//            btnRegister.isEnabled = true
//
//            result.onSuccess { userId ->
//                showToast(getString(R.string.register_success))
//
//                // Auto login after registration
//                val email = etEmail.text.toString().trim()
//                val password = etPassword.text.toString().trim()
//                viewModel.login(email, password)
//            }
//
//            result.onFailure { error ->
//                showToast(error.message ?: getString(R.string.register_failed))
//            }
//        }
//
//        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
//            result.onSuccess { user ->
//                preferencesManager.currentUserId = user.userId
//                preferencesManager.isLoggedIn = true
//
//                ReminderScheduler.scheduleReminders(
//                    requireContext(),
//                    user.userId,
//                    user.reminderIntervalMinutes
//                )
//
//                val intent = Intent(requireContext(), MainActivity::class.java)
//                startActivity(intent)
//                requireActivity().finish()
//            }
//        }
//    }
//
//    private fun setupListeners() {
//        etDateOfBirth.setOnClickListener {
//            showDatePicker()
//        }
//
//        btnRegister.setOnClickListener {
//            val username = etUsername.text.toString().trim()
//            val email = etEmail.text.toString().trim()
//            val password = etPassword.text.toString().trim()
//            val confirmPassword = etConfirmPassword.text.toString().trim()
//
//            if (validateInput(username, email, password, confirmPassword)) {
//                progressBar.visibility = View.VISIBLE
//                btnRegister.isEnabled = false
//                viewModel.register(username, email, password, selectedDateOfBirth)
//            }
//        }
//    }
//
//    private fun showDatePicker() {
//        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.YEAR, -18)
//
//        val datePickerDialog = DatePickerDialog(
//            requireContext(),
//            { _, year, month, dayOfMonth ->
//                val selectedCalendar = Calendar.getInstance()
//                selectedCalendar.set(year, month, dayOfMonth)
//                selectedDateOfBirth = selectedCalendar.timeInMillis
//
//                etDateOfBirth.setText("$dayOfMonth/${month + 1}/$year")
//            },
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//        )
//
//        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
//        datePickerDialog.show()
//    }
//
//    private fun validateInput(
//        username: String,
//        email: String,
//        password: String,
//        confirmPassword: String
//    ): Boolean {
//        if (username.isEmpty()) {
//            showToast(getString(R.string.error_username_empty))
//            return false
//        }
//
//        if (username.length < 3) {
//            showToast(getString(R.string.error_username_short))
//            return false
//        }
//
//        if (email.isEmpty()) {
//            showToast(getString(R.string.error_email_empty))
//            return false
//        }
//
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            showToast(getString(R.string.error_email_invalid))
//            return false
//        }
//
//        if (password.isEmpty()) {
//            showToast(getString(R.string.error_password_empty))
//            return false
//        }
//
//        if (password.length < 6) {
//            showToast(getString(R.string.error_password_short))
//            return false
//        }
//
//        if (password != confirmPassword) {
//            showToast(getString(R.string.error_password_mismatch))
//            return false
//        }
//
//        if (selectedDateOfBirth == 0L) {
//            showToast(getString(R.string.error_dob_empty))
//            return false
//        }
//
//        if (!validateAgeUseCase.execute(selectedDateOfBirth)) {
//            showToast(validateAgeUseCase.getErrorMessage())
//            return false
//        }
//
//        return true
//    }
//}
