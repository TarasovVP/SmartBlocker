package com.example.blacklister.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.example.blacklister.extensions.breakCallNougatAndLower
import com.example.blacklister.extensions.breakCallPieAndHigher
import com.example.blacklister.extensions.isPermissionAccepted
import com.google.gson.Gson

class CallReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        Log.e("callTAG", "CallReceiver onReceive intent $${Gson().toJson(intent)}")

        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (!context.isPermissionAccepted(Manifest.permission.READ_PHONE_STATE) ||
            !context.isPermissionAccepted(Manifest.permission.CALL_PHONE) || (telephony.callState != TelephonyManager.CALL_STATE_RINGING) || (!intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER))
        ) return

        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
       //TODO remove mock
        if ("+380633534322" == number) {
            breakCall(context)
        }
    }

    private fun breakCall(context: Context) {
        if (Build.VERSION.SDK_INT >= 28) {
            context.breakCallPieAndHigher()
        } else {
            context.breakCallNougatAndLower()
        }
    }
}