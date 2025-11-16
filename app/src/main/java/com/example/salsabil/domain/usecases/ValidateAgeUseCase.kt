package com.example.salsabil.domain.usecases


import java.util.Calendar

class ValidateAgeUseCase {

    fun execute(birthTimestamp: Long): Boolean {
        val age = calculateAge(birthTimestamp)
        return age >= 18
    }

    fun calculateAge(birthTimestamp: Long): Int {
        val birthCalendar = Calendar.getInstance()
        birthCalendar.timeInMillis = birthTimestamp

        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }

    fun getErrorMessage(): String {
        return "You must be at least 18 years old to use this app"
    }
}
