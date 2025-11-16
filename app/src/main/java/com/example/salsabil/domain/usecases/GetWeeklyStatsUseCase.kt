package com.example.salsabil.domain.usecases

import com.example.salsabil.data.local.entities.ConsumptionLog
import com.example.salsabil.domain.models.DayData
import java.text.SimpleDateFormat
import java.util.*

class GetWeeklyStatsUseCase {

    fun execute(logs: List<ConsumptionLog>): List<DayData> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val weekData = mutableListOf<DayData>()

        // Group logs by day
        val logsByDay = logs.groupBy { log ->
            calendar.timeInMillis = log.timestamp
            calendar.get(Calendar.DAY_OF_YEAR)
        }

        // Create data for last 7 days
        calendar.timeInMillis = System.currentTimeMillis()
        for (i in 6 downTo 0) {
            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            val dayName = dateFormat.format(calendar.time)
            val amount = logsByDay[dayOfYear]?.sumOf { it.amountMl } ?: 0

            weekData.add(
                DayData(
                    dayName = dayName,
                    amount = amount,
                    date = calendar.timeInMillis
                )
            )

            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        return weekData.reversed()
    }
}
