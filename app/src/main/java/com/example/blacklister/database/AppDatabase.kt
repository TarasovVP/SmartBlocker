package com.example.blacklister.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.model.CallLog
import com.example.blacklister.model.Contact

@Database(
    entities = [Contact::class, BlackNumber::class, CallLog::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringTypeConverters::class, CalendarConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun blackNumberDao(): BlackNumberDao
    abstract fun callLogDao(): CallLogDao
}