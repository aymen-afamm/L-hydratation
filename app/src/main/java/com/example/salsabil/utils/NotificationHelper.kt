package com.example.salsabil.utils


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.salsabil.R
import com.example.salsabil.ui.main.MainActivity

class NotificationHelper(private val context: Context) {

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                importance
            ).apply {
                description = "Reminders to drink water throughout the day"
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendHydrationReminder(userId: Long, remaining: Int, progress: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Constants.EXTRA_USER_ID, userId)
            putExtra(Constants.EXTRA_OPEN_DASHBOARD, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle(getPersonalizedTitle(progress))
            .setContentText(getPersonalizedMessage(remaining))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(getPersonalizedMessage(remaining)))
            .build()

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            try {
                NotificationManagerCompat.from(context)
                    .notify(Constants.REMINDER_NOTIFICATION_ID, notification)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

    }

    fun sendGoalAchievedNotification(userId: Long) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Constants.EXTRA_USER_ID, userId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_achievement)
            .setContentTitle("🎉 Goal Achieved!")
            .setContentText("Congratulations! You've reached your hydration goal today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            if (areNotificationsEnabled()) {
                try {
                    notify(Constants.ACHIEVEMENT_NOTIFICATION_ID, notification)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }

    }

    private fun getPersonalizedTitle(progress: Int): String {
        return when {
            progress < 25 -> "Time to hydrate! 💧"
            progress < 50 -> "Keep it up!"
            progress < 75 -> "You're doing great!"
            progress < 100 -> "Almost there!"
            else -> "Goal achieved! 🎉"
        }
    }

    private fun getPersonalizedMessage(remainingMl: Int): String {
        val liters = remainingMl / 1000f
        return when {
            remainingMl > 1500 -> "You still need to drink ${String.format("%.1f", liters)}L of water today. Stay hydrated!"
            remainingMl > 500 -> "Only ${remainingMl}ml left to reach your goal. You can do it!"
            else -> "Just ${remainingMl}ml more! You're almost there!"
        }
    }
}