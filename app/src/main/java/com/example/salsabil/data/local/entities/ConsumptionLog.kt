package com.example.salsabil.data.local.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "consumption_logs",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["timestamp"])
    ]
)
data class ConsumptionLog(
    @PrimaryKey(autoGenerate = true)
    val logId: Long = 0,
    val userId: Long,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String? = null,
    val logType: LogType = LogType.MANUAL
)

enum class LogType {
    MANUAL,
    REMINDER
}