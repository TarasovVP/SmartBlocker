package com.tarasovvp.smartblocker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.database.AppDatabase
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.Settings
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import java.util.*

class SmartBlockerApp : Application() {

    var database: AppDatabase? = null
    var auth: FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    var isNetworkAvailable: Boolean? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, AppDatabase::class.java, packageName)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.SERVER_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
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

    fun checkNetworkAvailable(): Boolean {
        if (isNetworkAvailable.isNotTrue()) {
            baseContext?.getString(R.string.app_network_unavailable_repeat).orEmpty().sendExceptionBroadCast()
            return true
        }
        return false
    }

    companion object {
        var instance: SmartBlockerApp? = null
    }
}