package com.example.salsabil.data.local.dao


import androidx.room.*
import com.example.salsabil.data.local.entities.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal): Long

    @Update
    suspend fun updateGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getGoalForDate(userId: Long, date: Long): Goal?

    @Query("SELECT * FROM goals WHERE userId = :userId AND date = :date LIMIT 1")
    fun getGoalForDateFlow(userId: Long, date: Long): Flow<Goal?>

    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    fun getRecentGoals(userId: Long, limit: Int = 30): Flow<List<Goal>>

    @Query("""
        SELECT * FROM goals 
        WHERE userId = :userId 
        AND date >= :startDate 
        AND date <= :endDate
        ORDER BY date DESC
    """)
    suspend fun getGoalsInRange(userId: Long, startDate: Long, endDate: Long): List<Goal>

    @Query("UPDATE goals SET achievedMl = :achievedMl, isAchieved = :isAchieved WHERE goalId = :goalId")
    suspend fun updateGoalProgress(goalId: Long, achievedMl: Int, isAchieved: Boolean)

    @Query("UPDATE goals SET achievedMl = :achievedMl, isAchieved = :isAchieved, achievedAt = :achievedAt WHERE goalId = :goalId")
    suspend fun markGoalAsAchieved(goalId: Long, achievedMl: Int, isAchieved: Boolean, achievedAt: Long)

    @Query("SELECT COUNT(*) FROM goals WHERE userId = :userId AND isAchieved = 1")
    fun getAchievedGoalsCount(userId: Long): Flow<Int>

    @Query("DELETE FROM goals WHERE userId = :userId")
    suspend fun deleteAllGoalsForUser(userId: Long)
}
