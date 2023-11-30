package com.huymee.android.criminalintent.database

import androidx.room.TypeConverter
import java.util.Date

class CrimeTypeConverter {
    @TypeConverter
    fun fromDate(date: Date): Long = date.time

    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date = Date(millisSinceEpoch)
}