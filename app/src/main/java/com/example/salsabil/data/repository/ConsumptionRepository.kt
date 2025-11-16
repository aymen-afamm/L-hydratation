package com.example.salsabil.data.repository


import com.example.salsabil.data.local.dao.ConsumptionLogDao
import com.example.salsabil.data.local.entities.ConsumptionLog
import com.example.salsabil.data.local.entities.LogType
import kotlinx.coroutines.flow.Flow

class ConsumptionRepository(private val consumptionLogDao: ConsumptionLogDao) {

    suspend fun logWaterIntake(
        userId: Long,
        amountMl: Int,
        note: String? = null,
        logType: LogType = LogType.MANUAL
    ): Long {
        val log = ConsumptionLog(
            userId = userId,
            amountMl = amountMl,
            note = note,
            logType = logType
        )
        return consumptionLogDao.insertLog(log)
    }

    suspend fun deleteLog(log: ConsumptionLog) {
        consumptionLogDao.deleteLog(log)
    }

    fun getAllLogsForUser(userId: Long): Flow<List<ConsumptionLog>> {
        return consumptionLogDao.getAllLogsForUser(userId)
    }

    fun getLogsForDay(userId: Long, startOfDay: Long, endOfDay: Long): Flow<List<ConsumptionLog>> {
        return consumptionLogDao.getLogsForDay(userId, startOfDay, endOfDay)
    }

    suspend fun getTotalForDay(userId: Long, startOfDay: Long, endOfDay: Long): Int {
        return consumptionLogDao.getTotalForDay(userId, startOfDay, endOfDay) ?: 0
    }

    fun getTotalForDayFlow(userId: Long, startOfDay: Long, endOfDay: Long): Flow<Int?> {
        return consumptionLogDao.getTotalForDayFlow(userId, startOfDay, endOfDay)
    }
}
