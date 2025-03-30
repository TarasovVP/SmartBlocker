package com.tarasovvp.smartblocker.utils.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.android.internal.telephony.ITelephony
import com.appsflyer.AppsFlyerLib
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.smartblocker.BuildConfig
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.END_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.GET_IT_TELEPHONY
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import dagger.hilt.android.testing.HiltTestApplication
import java.util.Locale

fun Context.registerForNetworkUpdates(isNetworkAvailable: (Boolean) -> Unit) {
    val networkRequest =
        NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isNetworkAvailable.invoke(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isNetworkAvailable.invoke(false)
            }
        }
    val connectivityManager =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        } else {
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
    connectivityManager.requestNetwork(networkRequest, networkCallback)
}

fun Application.isNetworkAvailable(): Boolean {
    return when (this) {
        is SmartBlockerApp -> isNetworkAvailable.isTrue()
        is HiltTestApplication -> true
        else -> false
    }
}

fun Application.initAppsFlyerLib() {
    AppsFlyerLib.getInstance().init(BuildConfig.APPS_FLYER_DEV_KEY, null, this)
    AppsFlyerLib.getInstance().waitForCustomerUserId(true)
    AppsFlyerLib.getInstance().start(this)
    AppsFlyerLib.getInstance().setCustomerUserId(BuildConfig.APPLICATION_ID)
}

fun Context.createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel =
            NotificationChannel(
                Constants.NOTIFICATION_CHANNEL,
                Constants.FOREGROUND_CALL_SERVICE,
                NotificationManager.IMPORTANCE_HIGH,
            )
        channel.lightColor = Color.BLUE
        channel.importance = NotificationManager.IMPORTANCE_NONE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        channel.setShowBadge(false)
        val service = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
    }
}

fun Context.notificationBuilder(): NotificationCompat.Builder {
    val notificationIntent = Intent(this, MainActivity::class.java)

    val pendingIntent =
        PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0,
        )
    val builder: NotificationCompat.Builder =
        NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)

    builder.setSmallIcon(R.drawable.ic_logo)
        .setColor(ContextCompat.getColor(this, R.color.cornflower_blue))
        .setContentTitle(getString(R.string.app_is_active))
        .setContentIntent(pendingIntent)

    return builder
}

fun TelephonyManager.isCallStateRinging(): Boolean {
    return callState == TelephonyManager.CALL_STATE_RINGING
}

fun TelephonyManager.isCallStateIdle(): Boolean {
    return callState == TelephonyManager.CALL_STATE_IDLE
}

fun Context.breakCallNougatAndLower() {
    val telephony = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    try {
        val c = Class.forName(telephony.javaClass.name)
        val m = c.getDeclaredMethod(GET_IT_TELEPHONY)
        m.isAccessible = true
        val telephonyService: ITelephony = m.invoke(telephony) as ITelephony
        telephonyService.endCall()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.breakCallPieAndHigher() {
    val telecomManager = this.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    try {
        telecomManager.javaClass.getMethod(END_CALL).invoke(telecomManager)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.setAppLocale(language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    return createConfigurationContext(config)
}

fun Context.isDarkMode(): Boolean {
    return when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
        Configuration.UI_MODE_NIGHT_YES -> true
        else -> false
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

@SuppressLint("ClickableViewAccessibility")
fun ViewGroup.hideKeyboardWithLayoutTouch() {
    setOnTouchListener { v, event ->
        hideKeyboard()
        v?.onTouchEvent(event) ?: true
    }
}

fun Activity.launchReviewFlow(result: (Result<Unit>) -> Unit) {
    ReviewManagerFactory.create(this).apply {
        val request = requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                launchReviewFlow(this@launchReviewFlow, task.result).addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener {
                    result.invoke(Result.Failure(it.localizedMessage))
                }
            } else {
                result.invoke(Result.Failure(task.exception?.localizedMessage))
            }
        }
    }
}

fun FirebaseAuth.isAuthorisedUser(): Boolean {
    return currentUser.isNotNull() && currentUser?.isAnonymous.isNotTrue()
}

fun FirebaseAuth.isGoogleAuthUser(isTestMode: Boolean): Boolean {
    if (isTestMode) return false
    currentUser?.providerData?.forEach {
        if (it.providerId == GoogleAuthProvider.PROVIDER_ID) return true
    }
    return false
}
