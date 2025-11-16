package com.example.salsabil.utils


object Constants {
    // Water amounts in ML
    const val SMALL_GLASS_ML = 250
    const val MEDIUM_GLASS_ML = 500
    const val LARGE_GLASS_ML = 750
    const val BOTTLE_ML = 1000

    // Default values
    const val DEFAULT_DAILY_GOAL_ML = 2000
    const val MIN_AGE = 18

    // Reminder intervals (in minutes)
    const val REMINDER_INTERVAL_1H = 60
    const val REMINDER_INTERVAL_2H = 120
    const val REMINDER_INTERVAL_3H = 180
    const val REMINDER_INTERVAL_4H = 240

    // Notification IDs
    const val NOTIFICATION_CHANNEL_ID = "hydration_reminders"
    const val NOTIFICATION_CHANNEL_NAME = "Hydration Reminders"
    const val REMINDER_NOTIFICATION_ID = 1001
    const val ACHIEVEMENT_NOTIFICATION_ID = 1002

    // WorkManager
    const val REMINDER_WORK_TAG = "hydration_reminder"
    const val WORK_INPUT_USER_ID = "USER_ID"

    // Intent extras
    const val EXTRA_USER_ID = "user_id"
    const val EXTRA_OPEN_DASHBOARD = "open_dashboard"

    // SharedPreferences
    const val PREFS_NAME = "salsabil_prefs"

    // Colors
    const val COLOR_GRADIENT_START = "#A4D7E1"
    const val COLOR_GRADIENT_END = "#4A90E2"
    const val COLOR_PRIMARY = "#4A90E2"
    const val COLOR_SUCCESS = "#7ED321"
    const val COLOR_TEXT_DARK = "#333333"
    const val COLOR_TEXT_LIGHT = "#FFFFFF"
}
