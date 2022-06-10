package com.tarasovvp.blacklister.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.CallLog
import com.tarasovvp.blacklister.model.Contact

@Database(
    entities = [Contact::class, BlackNumber::class, CallLog::class, BlockedCall::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun blackNumberDao(): BlackNumberDao
    abstract fun callLogDao(): CallLogDao
    abstract fun blockedCallDao(): BlockedCallDao
}