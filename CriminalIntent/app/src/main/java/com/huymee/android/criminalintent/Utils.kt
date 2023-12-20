package com.huymee.android.criminalintent

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DATE_FULL_FORMAT = "EEEE, MMM dd, yyyy"
private const val DATE_SHORT_FORMAT = "EEE, MMM, dd"
private const val TIME_FORMAT = "HH : mm"

object Utils {
    fun getFullFormattedDate(date: Date) = getFormattedDateString(date, DATE_FULL_FORMAT)

    fun getShortFormattedDate(date: Date) = getFormattedDateString(date, DATE_SHORT_FORMAT)

    fun getFormattedTime(date: Date) = getFormattedDateString(date, TIME_FORMAT)

    private fun getFormattedDateString(date: Date, pattern: String): String {
        return SimpleDateFormat(pattern, Locale.ENGLISH).format(date)
    }
}