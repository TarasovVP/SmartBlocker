package com.example.blacklister

import android.app.Application
import androidx.room.Room
import com.example.blacklister.database.AppDatabase
import com.example.blacklister.extensions.createNotificationChannel
import com.example.blacklister.local.Settings

class BlackListerApp : Application() {

    var database: AppDatabase? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, AppDatabase::class.java, packageName)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        Settings.loadSettingsHelper(this, this.packageName)
        createNotificationChannel()
    }

    companion object {
        var instance: BlackListerApp? = null
            private set
    }
}