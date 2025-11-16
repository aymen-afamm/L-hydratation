//package com.example.salsabil.ui.onboarding
//
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import androidx.viewpager2.widget.ViewPager2
//import com.google.android.material.button.MaterialButton
//import com.example.salsabil.R
//import com.example.salsabil.data.preferences.PreferencesManager
//import com.example.salsabil.ui.auth.AuthActivity
//
//class OnboardingActivity : AppCompatActivity() {
//
//    private lateinit var viewPager: ViewPager2
//    private lateinit var btnNext: MaterialButton
//    private lateinit var btnSkip: MaterialButton
//    private lateinit var preferencesManager: PreferencesManager
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_onboarding)
//
//        preferencesManager = PreferencesManager(this)
//
//        viewPager = findViewById(R.id.viewPagerOnboarding)
//        btnNext = findViewById(R.id.btnNext)
//        btnSkip = findViewById(R.id.btnSkip)
//
//        val adapter = OnboardingPagerAdapter(this)
//        viewPager.adapter = adapter
//
//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                if (position == 2) { // Last page
//                    btnNext.text = getString(R.string.get_started)
//                    btnSkip.visibility = View.GONE
//                } else {
//                    btnNext.text = getString(R.string.next)
//                    btnSkip.visibility = View.VISIBLE
//                }
//            }
//        })
//
//        btnNext.setOnClickListener {
//            if (viewPager.currentItem < 2) {
//                viewPager.currentItem += 1
//            } else {
//                completeOnboarding()
//            }
//        }
//
//        btnSkip.setOnClickListener {
//            completeOnboarding()
//        }
//    }
//
//    private fun completeOnboarding() {
//        preferencesManager.onboardingCompleted = true
//        startActivity(Intent(this, AuthActivity::class.java))
//        finish()
//    }
//}
