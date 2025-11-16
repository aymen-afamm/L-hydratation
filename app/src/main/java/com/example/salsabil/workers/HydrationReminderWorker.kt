package com.example.salsabil.workers


import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.salsabil.data.local.database.SalsabilDatabase
import com.example.salsabil.utils.Constants
import com.example.salsabil.utils.DateUtils
import com.example.salsabil.utils.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HydrationReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val database = SalsabilDatabase.getDatabase(context)
    private val notificationHelper = NotificationHelper(context)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val userId = inputData.getLong(Constants.WORK_INPUT_USER_ID, -1L)
            if (userId == -1L) {
                return@withContext Result.failure()
            }

            val user = database.userDao().getUserByIdSync(userId)
                ?: return@withContext Result.failure()

            if (!user.notificationsEnabled) {
                return@withContext Result.success()
            }

            val (startOfDay, endOfDay) = DateUtils.getTodayRange()
            val totalConsumed = database.consumptionLogDao()
                .getTotalForDay(userId, startOfDay, endOfDay) ?: 0

            val normalizedDate = DateUtils.normalizeToStartOfDay(System.currentTimeMillis())
            val todayGoal = database.goalDao().getGoalForDate(userId, normalizedDate)
                ?: createDefaultGoal(userId, normalizedDate, user.dailyGoalMl)

            val remaining = todayGoal.targetMl - totalConsumed

            if (remaining > 0) {
                notificationHelper.sendHydrationReminder(
                    userId = userId,
                    remaining = remaining,
                    progress = calculateProgress(totalConsumed, todayGoal.targetMl)
                )
            } else {
                notificationHelper.sendGoalAchievedNotification(userId)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun createDefaultGoal(userId: Long, date: Long, targetMl: Int): com.example.salsabil.data.local.entities.Goal {
        val goal = com.example.salsabil.data.local.entities.Goal(
            userId = userId,
            date = date,
            targetMl = targetMl
        )
        database.goalDao().insertGoal(goal)
        return goal
    }

    private fun calculateProgress(consumed: Int, target: Int): Int {
        return ((consumed.toFloat() / target.toFloat()) * 100).toInt().coerceIn(0, 100)
    }
}
