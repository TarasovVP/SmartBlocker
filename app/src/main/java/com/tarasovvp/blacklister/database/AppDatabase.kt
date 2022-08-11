package com.tarasovvp.blacklister.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tarasovvp.blacklister.model.*

@Database(
    entities = [LogCall::class, BlockedCall::class, Contact::class, BlackFilter::class, WhiteFilter::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logCallDao(): LogCallDao
    abstract fun blockedCallDao(): BlockedCallDao
    abstract fun contactDao(): ContactDao
    abstract fun blackFilterDao(): BlackFilterDao
    abstract fun whiteFilterDao(): WhiteFilterDao
}