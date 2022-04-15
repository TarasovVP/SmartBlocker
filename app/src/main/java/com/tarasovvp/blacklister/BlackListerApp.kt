package com.tarasovvp.blacklister

import android.app.Application
import androidx.room.Room
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.tarasovvp.blacklister.database.AppDatabase
import com.tarasovvp.blacklister.extensions.createNotificationChannel
import com.tarasovvp.blacklister.local.Settings
import com.google.firebase.analytics.FirebaseAnalytics

class BlackListerApp : Application() {

    var database: AppDatabase? = null
    private val interstitialAd: InterstitialAd? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, AppDatabase::class.java, packageName)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        MobileAds.initialize(this)
        FirebaseAnalytics.getInstance(this)
        Settings.loadSettingsHelper(this, this.packageName)
        createNotificationChannel()
    }

    companion object {
        var instance: BlackListerApp? = null
            private set
    }
}