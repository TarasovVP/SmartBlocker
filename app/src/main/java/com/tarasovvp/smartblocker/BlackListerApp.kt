package com.tarasovvp.smartblocker

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tarasovvp.smartblocker.database.AppDatabase
import com.tarasovvp.smartblocker.extensions.createNotificationChannel
import com.tarasovvp.smartblocker.extensions.isNotNull
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.registerForNetworkUpdates
import com.tarasovvp.smartblocker.local.Settings
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import java.util.*

class BlackListerApp : Application() {

    var database: AppDatabase? = null
    var auth: FirebaseAuth? = null
    var isNetworkAvailable: Boolean? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, AppDatabase::class.java, packageName)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        auth = Firebase.auth
        Log.e("authTAG", "BlackListerApp onCreate auth?.uid ${instance?.auth?.uid}")
        MobileAds.initialize(this)
        FirebaseAnalytics.getInstance(this)
        Settings.loadSettingsHelper(this, this.packageName)
        createNotificationChannel()
        if (SharedPreferencesUtil.appLang.isNullOrEmpty()) SharedPreferencesUtil.appLang =
            Locale.getDefault().language
        SharedPreferencesUtil.appTheme.apply {
            AppCompatDelegate.setDefaultNightMode(this)
        }
        registerForNetworkUpdates { isAvailable ->
            isNetworkAvailable = isAvailable
        }
    }

    fun isLoggedInUser() = auth?.currentUser.isNotNull().isTrue()

    companion object {
        var instance: BlackListerApp? = null
    }
}