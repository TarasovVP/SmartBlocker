package com.example.blacklister.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.provider.ContactsContract
import android.telephony.CellInfo
import android.telephony.PhoneStateListener
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager
import android.util.Log
import com.example.blacklister.BlackListerApp
import com.example.blacklister.extensions.breakCallNougatAndLower
import com.example.blacklister.extensions.breakCallPieAndHigher
import com.example.blacklister.extensions.deleteLastMissedCall
import com.example.blacklister.extensions.isPermissionAccepted
import com.google.gson.Gson
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


open class CallReceiver(private val phoneListener: (String) -> Unit) : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        Log.e("callTAG", "CallReceiver intent ${Gson().toJson(intent)}")

        if (!context.isPermissionAccepted(Manifest.permission.READ_PHONE_STATE) || !context.isPermissionAccepted(Manifest.permission.CALL_PHONE) || !intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) return

        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val phone = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: ""
        val blackNumber = BlackListerApp.instance?.database?.blackNumberDao()?.getBlackNumber(phone)
        Log.e("callTAG", "CallReceiver onReceive telephony.callState ${telephony.callState}")
        blackNumber ?: return
        if (telephony.callState == TelephonyManager.CALL_STATE_RINGING) {
            Log.e("callTAG", "CallReceiver telephony.callState == TelephonyManager.CALL_STATE_RINGING phone $phone blackNumber.blackNumber ${blackNumber.blackNumber}")
            phoneListener.invoke("phone ${blackNumber.blackNumber}")
            breakCall(context)
        } else if (telephony.callState == TelephonyManager.CALL_STATE_IDLE) {
                Executors.newSingleThreadScheduledExecutor().schedule({
                    Log.e("callTAG", "CallReceiver telephony.callState == TelephonyManager.CALL_STATE_IDLE contact.phone $phone")
                    context.deleteLastMissedCall(phone)
                }, 1, TimeUnit.SECONDS)
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