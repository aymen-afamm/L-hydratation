package com.example.salsabil.workers


import android.content.Context
import androidx.work.*
import com.example.salsabil.utils.Constants
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleReminders(
        context: Context,
        userId: Long,
        intervalMinutes: Int = 120
    ) {
        cancelReminders(context)

        val inputData = workDataOf(Constants.WORK_INPUT_USER_ID to userId)

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()

        val reminderRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            intervalMinutes.toLong(),
            TimeUnit.MINUTES,
            15,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag(Constants.REMINDER_WORK_TAG)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )

            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "hydration_reminder_$userId",
            ExistingPeriodicWorkPolicy.REPLACE,
            reminderRequest
        )
    }

    fun scheduleSmartReminders(
        context: Context,
        userId: Long,
        preferredHours: List<Int> = listOf(8, 10, 12, 14, 16, 18, 20)
    ) {
        cancelReminders(context)

        preferredHours.forEach { hour ->
            val delay = calculateDelayUntilHour(hour)

            val inputData = workDataOf(Constants.WORK_INPUT_USER_ID to userId)

            val reminderRequest = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
                .setInputData(inputData)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(Constants.REMINDER_WORK_TAG)
                .addTag("reminder_hour_$hour")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "smart_reminder_${userId}_$hour",
                ExistingWorkPolicy.REPLACE,
                reminderRequest
            )
        }
    }

    fun cancelReminders(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(Constants.REMINDER_WORK_TAG)
    }

    fun updateReminderInterval(context: Context, userId: Long, newIntervalMinutes: Int) {
        scheduleReminders(context, userId, newIntervalMinutes)
    }

    private fun calculateDelayUntilHour(targetHour: Int): Long {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = now
        calendar.set(Calendar.HOUR_OF_DAY, targetHour)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        var targetTime = calendar.timeInMillis

        if (targetTime <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            targetTime = calendar.timeInMillis
        }

        return targetTime - now
    }
}
