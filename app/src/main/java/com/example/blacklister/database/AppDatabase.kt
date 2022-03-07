package com.example.blacklister.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.blacklister.model.Contact

@Database(
    entities = [Contact::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}