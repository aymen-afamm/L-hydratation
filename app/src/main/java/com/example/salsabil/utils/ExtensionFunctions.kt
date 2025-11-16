package com.example.salsabil.utils


import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().showToast(message, duration)
}

fun Int.toFormattedAmount(): String {
    return when {
        this >= 1000 -> "${this / 1000f}L"
        else -> "${this}ml"
    }
}

fun Long.toFormattedDate(): String {
    return DateUtils.formatDate(this)
}

fun Long.toFormattedTime(): String {
    return DateUtils.formatTime(this)
}