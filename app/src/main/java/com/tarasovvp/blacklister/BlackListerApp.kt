package com.tarasovvp.blacklister

import android.app.Application
import androidx.room.Room
import com.tarasovvp.blacklister.database.AppDatabase
import com.tarasovvp.blacklister.extensions.createNotificationChannel
import com.tarasovvp.blacklister.local.Settings
import com.google.firebase.analytics.FirebaseAnalytics

class BlackListerApp : Application() {

    var database: AppDatabase? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, AppDatabase::class.java, packageName)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        FirebaseAnalytics.getInstance(this)
        Settings.loadSettingsHelper(this, this.packageName)
        createNotificationChannel()
    }

    companion object {
        var instance: BlackListerApp? = null
            private set
    }
}