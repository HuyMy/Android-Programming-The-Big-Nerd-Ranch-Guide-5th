package com.huymee.android.criminalintent

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    fun getFormattedDate(date: Date): String {
        val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH)
        return dateFormat.format(date)
    }
}