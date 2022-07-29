package com.tarasovvp.blacklister.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tarasovvp.blacklister.model.*

@Database(
    entities = [Contact::class, BlackNumber::class, LogCall::class, BlockedCall::class, WhiteNumber::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun logCallDao(): LogCallDao
    abstract fun blockedCallDao(): BlockedCallDao
    abstract fun blackNumberDao(): BlackNumberDao
    abstract fun whiteNumberDao(): WhiteNumberDao
}