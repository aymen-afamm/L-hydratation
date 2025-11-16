package com.example.salsabil.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.salsabil.data.local.dao.ConsumptionLogDao
import com.example.salsabil.data.local.dao.GoalDao
import com.example.salsabil.data.local.dao.UserDao
import com.example.salsabil.data.local.entities.User
import com.example.salsabil.data.local.entities.Goal
import com.example.salsabil.data.local.entities.ConsumptionLog



@Database(
    entities = [User::class, ConsumptionLog::class, Goal::class],
    version = 1,
    exportSchema = true
)
abstract class SalsabilDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun consumptionLogDao(): ConsumptionLogDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: SalsabilDatabase? = null

        fun getDatabase(context: Context): SalsabilDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SalsabilDatabase::class.java,
                    "salsabil_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}