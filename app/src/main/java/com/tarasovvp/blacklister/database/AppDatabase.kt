package com.tarasovvp.blacklister.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tarasovvp.blacklister.model.*

@Database(
    entities = [Contact::class, BlackNumber::class, CallLog::class, BlockedCall::class, WhiteNumber::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun blackNumberDao(): BlackNumberDao
    abstract fun callLogDao(): CallLogDao
    abstract fun blockedCallDao(): BlockedCallDao
    abstract fun whiteNumberDao(): WhiteNumberDao
}