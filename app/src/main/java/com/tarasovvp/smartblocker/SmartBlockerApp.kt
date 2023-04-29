package com.tarasovvp.smartblocker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.*

@HiltAndroidApp
class SmartBlockerApp : Application() {

    var firebaseAuth: FirebaseAuth? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var isNetworkAvailable: Boolean? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        firebaseAuth = Firebase.auth
        firebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.REALTIME_DATABASE)
        SharedPrefs.init(this)
        MobileAds.initialize(this)
        FirebaseAnalytics.getInstance(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        createNotificationChannel()
        if (SharedPrefs.appLang.isNullOrEmpty()) SharedPrefs.appLang = Locale.getDefault().language
        SharedPrefs.appTheme?.let { AppCompatDelegate.setDefaultNightMode(it) }
        registerForNetworkUpdates { isAvailable ->
            isNetworkAvailable = isAvailable
        }
    }

    fun isLoggedInUser() = firebaseAuth?.currentUser.isNotNull().isTrue()

    fun checkNetworkUnAvailable(): Boolean {
        if (isNetworkAvailable.isNotTrue()) {
            baseContext?.getString(R.string.app_network_unavailable_repeat)?.sendExceptionBroadCast()
            return true
        }
        return false
    }

    companion object {
        var instance: SmartBlockerApp? = null
    }
}