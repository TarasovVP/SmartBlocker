package com.tarasovvp.blacklister

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.room.Room
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tarasovvp.blacklister.database.AppDatabase
import com.tarasovvp.blacklister.extensions.createNotificationChannel
import com.tarasovvp.blacklister.extensions.setAppLocale
import com.tarasovvp.blacklister.local.Settings
import com.tarasovvp.blacklister.local.SharedPreferencesUtil

class BlackListerApp : Application() {

    var database: AppDatabase? = null
    var auth: FirebaseAuth? = null
    private val interstitialAd: InterstitialAd? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, AppDatabase::class.java, packageName)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        auth = Firebase.auth
        MobileAds.initialize(this)
        FirebaseAnalytics.getInstance(this)
        Settings.loadSettingsHelper(this, this.packageName)
        createNotificationChannel()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper(newBase.setAppLocale(SharedPreferencesUtil.appLang)))
    }

    companion object {
        var instance: BlackListerApp? = null
    }
}