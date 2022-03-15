package com.example.blacklister.utils

import android.R
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.blacklister.constants.Constants.FOREGROUND_ID
import com.example.blacklister.constants.Constants.NOTIFICATION_CHANNEL
import com.example.blacklister.constants.Constants.PHONE_STATE
import com.example.blacklister.ui.MainActivity
import java.util.function.Consumer


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

    private fun notificationBuilder(): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) FLAG_IMMUTABLE else 0
            )
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)

        builder.setSmallIcon(R.drawable.ic_delete)
            .setContentTitle("Blacklister")
            .setContentText("")
            .setSmallIcon(R.drawable.ic_delete)
            .setContentIntent(pendingIntent)

        return builder
    }
}