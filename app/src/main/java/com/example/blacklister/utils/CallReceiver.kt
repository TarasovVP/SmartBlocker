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
import com.example.blacklister.ui.BlackListerApp
import com.google.gson.Gson

class CallReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (!context.isPermissionAccepted(Manifest.permission.READ_PHONE_STATE) ||
            !context.isPermissionAccepted(Manifest.permission.CALL_PHONE) || (telephony.callState != TelephonyManager.CALL_STATE_RINGING) || (!intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER))
        ) return

        val phone = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: ""
        if (BlackListerApp.instance?.database?.contactDao()?.getContactByPhone(phone)?.isBlackList == true) breakCall(context)
    }

    private fun breakCall(context: Context) {
        if (Build.VERSION.SDK_INT >= 28) {
            context.breakCallPieAndHigher()
        } else {
            context.breakCallNougatAndLower()
        }
    }
}