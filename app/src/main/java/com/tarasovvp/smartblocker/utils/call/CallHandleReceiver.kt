package com.tarasovvp.smartblocker.utils.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CallHandleReceiver(private val callListener: () -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        callListener.invoke()
    }
}