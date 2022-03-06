package com.example.blacklister.utils

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import com.android.internal.telephony.ITelephony
import com.example.blacklister.extensions.isPermissionAccepted
import com.google.gson.Gson

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        Log.e("callTAG", "CallReceiver onReceive intent $${Gson().toJson(intent)}")
        if (!context.isPermissionAccepted(Manifest.permission.READ_PHONE_STATE) ||
            !context.isPermissionAccepted(Manifest.permission.CALL_PHONE)
        ) return

        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (telephony.callState != TelephonyManager.CALL_STATE_RINGING) {
            return
        }
        if (!intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
            Log.d(TAG, "Event had no incoming_number metadata. Letting it keep ringing...")
            return
        }
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        Log.e("callTAG", "number $number")
    }

    private fun breakCallNougatAndLower(context: Context) {
        Log.e(TAG, "CallReceiver breakCallNougatAndLower ")
        Log.d(TAG, "Trying to break call for Nougat and lower with TelephonyManager.")
        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            val c = Class.forName(telephony.javaClass.name)
            val m = c.getDeclaredMethod("getITelephony")
            m.isAccessible = true
            val telephonyService: ITelephony = m.invoke(telephony) as ITelephony
            telephonyService.endCall()
            Log.d(TAG, "Invoked 'endCall' on TelephonyService.")
        } catch (e: Exception) {
            Log.e(TAG, "Could not end call. Check stdout for more info.")
            e.printStackTrace()
        }
    }

    private fun breakCallPieAndHigher(context: Context) {
        Log.e(TAG, "CallReceiver breakCallPieAndHigher ")
        Log.d(TAG, "Trying to break call for Pie and higher with TelecomManager.")
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        try {
            telecomManager.javaClass.getMethod("endCall").invoke(telecomManager)
            Log.d(TAG, "Invoked 'endCall' on TelecomManager.")
        } catch (e: Exception) {
            Log.e(TAG, "Could not end call. Check stdout for more info.")
            e.printStackTrace()
        }
    }

    private fun breakCall(context: Context) {
        if (!context.isPermissionAccepted(Manifest.permission.CALL_PHONE)) {
            return
        }
        if (Build.VERSION.SDK_INT >= 28) {
            breakCallPieAndHigher(context)
        } else {
            breakCallNougatAndLower(context)
        }
    }

    private fun breakCallAndNotify(context: Context, number: String?, name: String?) {
        Log.e(TAG, "CallReceiver breakCallAndNotify ")
        breakCall(context)
    }

    companion object {
        private val TAG = CallReceiver::class.java.name
    }
}