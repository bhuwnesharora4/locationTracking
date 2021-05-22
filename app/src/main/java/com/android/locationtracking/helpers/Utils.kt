package com.android.locationtracking.helpers

import android.text.format.DateFormat
import java.util.*

object Utils {
    fun getDate(time: Long): String? {
        val cal: Calendar = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = time
        return DateFormat.format("dd-MM-yyyy (HH:mm:ss)", cal).toString()
    }
}