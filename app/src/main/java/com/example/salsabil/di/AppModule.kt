package com.example.salsabil.di


import android.content.Context
import com.example.salsabil.data.local.database.SalsabilDatabase
import com.example.salsabil.data.preferences.PreferencesManager
import com.example.salsabil.data.repository.ConsumptionRepository
import com.example.salsabil.data.repository.GoalRepository
import com.example.salsabil.data.repository.UserRepository

/**
 * Manual Dependency Injection Container
 * Provides singleton instances of repositories and database
 */
object AppModule {

    @Volatile
    private var database: SalsabilDatabase? = null

    @Volatile
    private var userRepository: UserRepository? = null

    @Volatile
    private var consumptionRepository: ConsumptionRepository? = null

    @Volatile
    private var goalRepository: GoalRepository? = null

    @Volatile
    private var preferencesManager: PreferencesManager? = null

    /**
     * Get or create SalsabilDatabase instance
     */
    fun provideDatabase(context: Context): SalsabilDatabase {
        return database ?: synchronized(this) {
            database ?: SalsabilDatabase.getDatabase(context.applicationContext).also {
                database = it
            }
        }
    }

    /**
     * Get or create UserRepository instance
     */
    fun provideUserRepository(context: Context): UserRepository {
        return userRepository ?: synchronized(this) {
            userRepository ?: UserRepository(
                provideDatabase(context).userDao()
            ).also {
                userRepository = it
            }
        }
    }

    /**
     * Get or create ConsumptionRepository instance
     */
    fun provideConsumptionRepository(context: Context): ConsumptionRepository {
        return consumptionRepository ?: synchronized(this) {
            consumptionRepository ?: ConsumptionRepository(
                provideDatabase(context).consumptionLogDao()
            ).also {
                consumptionRepository = it
            }
        }
    }

    /**
     * Get or create GoalRepository instance
     */
    fun provideGoalRepository(context: Context): GoalRepository {
        return goalRepository ?: synchronized(this) {
            goalRepository ?: GoalRepository(
                provideDatabase(context).goalDao()
            ).also {
                goalRepository = it
            }
        }
    }

    /**
     * Get or create PreferencesManager instance
     */
    fun providePreferencesManager(context: Context): PreferencesManager {
        return preferencesManager ?: synchronized(this) {
            preferencesManager ?: PreferencesManager(
                context.applicationContext
            ).also {
                preferencesManager = it
            }
        }
    }

    /**
     * Clear all cached instances (useful for testing)
     */
    fun clear() {
        database = null
        userRepository = null
        consumptionRepository = null
        goalRepository = null
        preferencesManager = null
    }
}