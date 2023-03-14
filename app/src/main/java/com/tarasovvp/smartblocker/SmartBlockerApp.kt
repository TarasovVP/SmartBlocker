package com.tarasovvp.smartblocker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.SharedPrefs
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class SmartBlockerApp : Application() {


    var auth: FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    var isNetworkAvailable: Boolean? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        auth = Firebase.auth
        googleSignInClient = this.googleSignInClient()
        SharedPrefs.init(this)
        MobileAds.initialize(this)
        FirebaseAnalytics.getInstance(this)
        createNotificationChannel()
        if (SharedPrefs.appLang.isNullOrEmpty()) SharedPrefs.appLang = Locale.getDefault().language
        SharedPrefs.appTheme?.let { AppCompatDelegate.setDefaultNightMode(it) }
        registerForNetworkUpdates { isAvailable ->
            isNetworkAvailable = isAvailable
        }
    }

    fun isLoggedInUser() = auth?.currentUser.isNotNull().isTrue()

    fun checkNetworkAvailable(): Boolean {
        if (isNetworkAvailable.isNotTrue()) {
            baseContext?.getString(R.string.app_network_unavailable_repeat).orEmpty()
                .sendExceptionBroadCast()
            return true
        }
        return false
    }

    companion object {
        var instance: SmartBlockerApp? = null
    }
}