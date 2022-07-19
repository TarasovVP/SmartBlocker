package com.tarasovvp.blacklister.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tarasovvp.blacklister.constants.Constants.EXCEPTION

class ExceptionReceiver(private val exceptionListener: (String) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        exceptionListener.invoke(intent.getStringExtra(EXCEPTION).orEmpty())
    }
}