package com.huymee.android.criminalintent

import android.app.Application
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DATE_FULL_FORMAT = "EEEE, MMM dd, yyyy"
private const val DATE_SHORT_FORMAT = "EEE, MMM, dd"
private const val TIME_FORMAT = "HH : mm"
private const val PREFERENCE_FILE_KEY = "com.huymee.android.criminalintent.shared_preference"

object Utils {
    fun getFullFormattedDate(date: Date) = getFormattedDateString(date, DATE_FULL_FORMAT)

    private fun getShortFormattedDate(date: Date) = getFormattedDateString(date, DATE_SHORT_FORMAT)

    fun getFormattedTime(date: Date) = getFormattedDateString(date, TIME_FORMAT)

    private fun getFormattedDateString(date: Date, pattern: String): String {
        return SimpleDateFormat(pattern, Locale.ENGLISH).format(date)
    }

    fun saveBooleanPreference(application: Application?, key: String, value: Boolean) {
        val sharedPref = application?.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)
            ?: return
        with(sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun getBooleanPreference(application: Application?, key: String): Boolean {
        val sharedPref = application?.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)
            ?: return false
        return sharedPref.getBoolean(key, false)
    }

    fun getCrimeReport(context: Context, crime: Crime): String {
        val solvedString = if (crime.isSolved) {
            context.getString(R.string.crime_report_solved)
        } else {
            context.getString(R.string.crime_report_unsolved)
        }

        val dateString = getShortFormattedDate(crime.date)
        val suspectText = if (crime.suspect.isBlank()) {
            context.getString(R.string.crime_report_no_suspect)
        } else {
            context.getString(R.string.crime_report_suspect, crime.suspect)
        }

        return context.getString(
            R.string.crime_report,
            crime.title, dateString, solvedString, suspectText
        )
    }
}