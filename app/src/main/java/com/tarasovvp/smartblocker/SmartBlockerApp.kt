package com.tarasovvp.smartblocker

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class SmartBlockerApp : Application() {

    var isNetworkAvailable: Boolean? = null

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        FirebaseAnalytics.getInstance(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        createNotificationChannel()
        registerForNetworkUpdates { isAvailable ->
            isNetworkAvailable = isAvailable
        }
    }
}