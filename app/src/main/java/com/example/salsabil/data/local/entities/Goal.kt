package com.example.salsabil.data.local.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "goals",
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
        Index(value = ["date"])
    ]
)
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val goalId: Long = 0,
    val userId: Long,
    val date: Long,
    val targetMl: Int,
    val achievedMl: Int = 0,
    val isAchieved: Boolean = false,
    val achievedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)