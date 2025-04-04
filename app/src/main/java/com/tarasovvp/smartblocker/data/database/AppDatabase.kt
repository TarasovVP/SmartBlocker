package com.tarasovvp.smartblocker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tarasovvp.smartblocker.BuildConfig
import com.tarasovvp.smartblocker.data.database.dao.ContactDao
import com.tarasovvp.smartblocker.data.database.dao.CountryCodeDao
import com.tarasovvp.smartblocker.data.database.dao.FilterDao
import com.tarasovvp.smartblocker.data.database.dao.FilteredCallDao
import com.tarasovvp.smartblocker.data.database.dao.LogCallDao
import com.tarasovvp.smartblocker.domain.entities.dbentities.Contact
import com.tarasovvp.smartblocker.domain.entities.dbentities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import com.tarasovvp.smartblocker.domain.entities.dbentities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.dbentities.LogCall
import com.tarasovvp.smartblocker.domain.entities.dbviews.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.dbviews.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.dbviews.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.utils.extensions.supportFactory

@Database(
    entities = [LogCall::class, FilteredCall::class, Contact::class, Filter::class, CountryCode::class],
    views = [ContactWithFilter::class, CallWithFilter::class, FilterWithFilteredNumber::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logCallDao(): LogCallDao

    abstract fun filteredCallDao(): FilteredCallDao

    abstract fun contactDao(): ContactDao

    abstract fun filterDao(): FilterDao

    abstract fun countryCodeDao(): CountryCodeDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, appContext.packageName)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .openHelperFactory(BuildConfig.REALTIME_DATABASE.supportFactory())
                .build()
    }
}
