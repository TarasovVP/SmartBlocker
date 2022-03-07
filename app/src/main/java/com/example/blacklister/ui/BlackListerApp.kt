package com.example.blacklister.ui

import android.app.Application
import androidx.room.Room
import com.example.blacklister.database.AppDatabase

class BlackListerApp : Application() {

    var database: AppDatabase? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, AppDatabase::class.java, packageName)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    companion object {
        var instance: BlackListerApp? = null
            private set
    }
}