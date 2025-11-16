package com.example.salsabil.ui.splash


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.salsabil.R
import com.example.salsabil.data.preferences.PreferencesManager
import com.example.salsabil.ui.auth.AuthActivity
import com.example.salsabil.ui.main.MainActivity
import com.example.salsabil.ui.onboarding.OnboardingActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager
    private val splashDelay = 2000L // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        preferencesManager = PreferencesManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, splashDelay)
    }

    private fun navigateToNextScreen() {
        val intent = when {
            !preferencesManager.onboardingCompleted -> {
                Intent(this, OnboardingActivity::class.java)
            }
            !preferencesManager.isLoggedIn -> {
                Intent(this, AuthActivity::class.java)
            }
            else -> {
                Intent(this, MainActivity::class.java).apply {
                    putExtra("USER_ID", preferencesManager.currentUserId)
                }
            }
        }

        startActivity(intent)
        finish()
    }
}