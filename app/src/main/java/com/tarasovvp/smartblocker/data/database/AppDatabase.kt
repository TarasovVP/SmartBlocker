package com.tarasovvp.smartblocker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tarasovvp.smartblocker.data.database.dao.*
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.*

@Database(
    entities = [LogCall::class, FilteredCall::class, Contact::class, Filter::class, CountryCode::class],
    views = [ContactWithFilter::class, LogCallWithFilter::class, FilteredCallWithFilter::class, FilterWithCountryCode::class],
    version = 1,
    exportSchema = false
)
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