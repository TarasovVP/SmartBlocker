package com.tarasovvp.blacklister.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

open class CallHandleReceiver(private val callListener: (String) -> Unit) : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("callReceiveTAG", "CallHandleReceiver callListener.invoke()")
        callListener.invoke("callReceiveTAG")
    }
}