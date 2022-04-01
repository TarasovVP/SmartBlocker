package com.tarasovvp.blacklister.utils

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.tarasovvp.blacklister.constants.Constants.FOREGROUND_ID
import com.tarasovvp.blacklister.constants.Constants.PHONE_STATE
import com.tarasovvp.blacklister.extensions.notificationBuilder


class ForegroundCallService : Service() {

    private var callReceiver: CallReceiver? = null
    private var notificationBuilder: NotificationCompat.Builder? = null

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        notificationBuilder = notificationBuilder()
        registerScreenOffReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(callReceiver)
        callReceiver = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FOREGROUND_ID, notificationBuilder?.build())
        return START_STICKY
    }

    private fun registerScreenOffReceiver() {
        callReceiver = CallReceiver { phone ->
            notificationBuilder?.setContentText(phone)
            startForeground(FOREGROUND_ID, notificationBuilder?.build())
        }
        val filter = IntentFilter(PHONE_STATE)
        registerReceiver(callReceiver, filter)
    }
}