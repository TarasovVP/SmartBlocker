package com.tarasovvp.smartblocker.infrastructure.services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PHONE_STATE
import com.tarasovvp.smartblocker.infrastructure.receivers.CallReceiver
import com.tarasovvp.smartblocker.utils.extensions.notificationBuilder

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
        val countDownTimer = object : CountDownTimer(1000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                notificationBuilder?.setContentText(formatTime(secondsRemaining))
                startForeground(Constants.FOREGROUND_ID, notificationBuilder?.build())
            }

            override fun onFinish() {
                notificationBuilder?.setContentText("Countdown Finished")
                startForeground(Constants.FOREGROUND_ID, notificationBuilder?.build())
                stopSelf()
            }
        }
        countDownTimer.start()
        return START_STICKY
    }

    fun formatTime(milliseconds: Long): String {
        return "Time: ${milliseconds / 60}:${milliseconds%60}"
    }

    private fun registerScreenOffReceiver() {
        callReceiver = CallReceiver()
        val filter = IntentFilter(PHONE_STATE)
        registerReceiver(callReceiver, filter)
    }
}