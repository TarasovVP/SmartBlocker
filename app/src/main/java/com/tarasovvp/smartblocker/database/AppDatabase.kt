package com.tarasovvp.smartblocker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tarasovvp.smartblocker.database.dao.*
import com.tarasovvp.smartblocker.models.*

@Database(
    entities = [LogCall::class, FilteredCall::class, Contact::class, Filter::class, CountryCode::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(CountryCodeConverter::class, FilterConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logCallDao(): LogCallDao
    abstract fun filteredCallDao(): FilteredCallDao
    abstract fun contactDao(): ContactDao
    abstract fun filterDao(): FilterDao
    abstract fun countryCodeDao(): CountryCodeDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, appContext.packageName)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
    }
}