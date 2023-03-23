package com.tarasovvp.smartblocker.infrastructure.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.EXCEPTION

class ExceptionReceiver(private val exceptionListener: (String) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        exceptionListener.invoke(intent.getStringExtra(EXCEPTION).orEmpty())
    }
}