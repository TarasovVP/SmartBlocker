package com.tarasovvp.smartblocker

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.tarasovvp.smartblocker.utils.extensions.createNotificationChannel
import com.tarasovvp.smartblocker.utils.extensions.registerForNetworkUpdates
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartBlockerApp : Application() {

    var isNetworkAvailable: Boolean? = null

    override fun onCreate() {
        super.onCreate()
        //MobileAds.initialize(this)
        FirebaseAnalytics.getInstance(this)
        createNotificationChannel()
        registerForNetworkUpdates { isAvailable ->
            isNetworkAvailable = isAvailable
        }
    }
}