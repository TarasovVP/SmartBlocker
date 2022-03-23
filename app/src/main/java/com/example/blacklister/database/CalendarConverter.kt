package com.example.blacklister.database

import androidx.room.TypeConverter
import java.util.*

object CalendarConverter {
    @TypeConverter
    fun toCalendar(timestamp: Long?): Calendar? {
        if (timestamp == null) return null
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar
    }

    @TypeConverter
    fun toTimestamp(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }
}