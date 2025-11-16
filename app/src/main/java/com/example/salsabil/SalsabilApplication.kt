package com.example.salsabil


import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager

class SalsabilApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize WorkManager
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()

        WorkManager.initialize(this, config)
    }
}