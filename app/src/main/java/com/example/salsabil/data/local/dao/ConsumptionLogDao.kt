package com.example.salsabil.data.local.dao


import androidx.room.*
import com.example.salsabil.data.local.entities.ConsumptionLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsumptionLogDao {
    @Insert
    suspend fun insertLog(log: ConsumptionLog): Long

    @Delete
    suspend fun deleteLog(log: ConsumptionLog)

    @Query("SELECT * FROM consumption_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllLogsForUser(userId: Long): Flow<List<ConsumptionLog>>

    @Query("""
        SELECT * FROM consumption_logs 
        WHERE userId = :userId 
        AND timestamp >= :startOfDay 
        AND timestamp < :endOfDay
        ORDER BY timestamp DESC
    """)
    fun getLogsForDay(userId: Long, startOfDay: Long, endOfDay: Long): Flow<List<ConsumptionLog>>

    @Query("""
        SELECT SUM(amountMl) FROM consumption_logs 
        WHERE userId = :userId 
        AND timestamp >= :startOfDay 
        AND timestamp < :endOfDay
    """)
    suspend fun getTotalForDay(userId: Long, startOfDay: Long, endOfDay: Long): Int?

    @Query("""
        SELECT SUM(amountMl) FROM consumption_logs 
        WHERE userId = :userId 
        AND timestamp >= :startOfDay 
        AND timestamp < :endOfDay
    """)
    fun getTotalForDayFlow(userId: Long, startOfDay: Long, endOfDay: Long): Flow<Int?>

    @Query("""
        SELECT * FROM consumption_logs 
        WHERE userId = :userId 
        AND timestamp >= :startOfWeek 
        AND timestamp < :endOfWeek
        ORDER BY timestamp ASC
    """)
    suspend fun getLogsForWeek(userId: Long, startOfWeek: Long, endOfWeek: Long): List<ConsumptionLog>

    @Query("DELETE FROM consumption_logs WHERE userId = :userId")
    suspend fun deleteAllLogsForUser(userId: Long)
}