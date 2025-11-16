package com.example.salsabil.ui.main


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.salsabil.R
import com.example.salsabil.data.preferences.PreferencesManager
import com.example.salsabil.ui.main.account.AccountFragment
import com.example.salsabil.ui.main.dashboard.DashboardFragment
import com.example.salsabil.ui.main.goals.GoalsFragment
import com.example.salsabil.ui.main.tips.TipsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var preferencesManager: PreferencesManager
    private var currentUserId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferencesManager = PreferencesManager(this)
        currentUserId = intent.getLongExtra("USER_ID", preferencesManager.currentUserId)

        bottomNav = findViewById(R.id.bottomNavigation)

        // Load Dashboard by default
        if (savedInstanceState == null) {
            val openDashboard = intent.getBooleanExtra("OPEN_DASHBOARD", true)
            if (openDashboard) {
                replaceFragment(DashboardFragment.newInstance(currentUserId))
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_dashboard -> DashboardFragment.newInstance(currentUserId)
                R.id.nav_goals -> GoalsFragment.newInstance(currentUserId)
                R.id.nav_tips -> TipsFragment.newInstance(currentUserId)
                R.id.nav_account -> AccountFragment.newInstance(currentUserId)
                else -> null
            }

            fragment?.let {
                replaceFragment(it)
                true
            } ?: false
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}