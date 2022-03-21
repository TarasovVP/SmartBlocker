package com.example.blacklister.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import com.example.blacklister.BlackListerApp
import com.example.blacklister.extensions.breakCallNougatAndLower
import com.example.blacklister.extensions.breakCallPieAndHigher
import com.example.blacklister.extensions.deleteLastMissedCall
import com.example.blacklister.utils.PermissionUtil.checkPermissions
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


open class CallReceiver(private val phoneListener: (String) -> Unit) : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        if (!context.checkPermissions() || !intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) return

        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val phone = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: ""
        val blackNumber = BlackListerApp.instance?.database?.blackNumberDao()?.getBlackNumber(phone)
        blackNumber ?: return
        if (telephony.callState == TelephonyManager.CALL_STATE_RINGING) {
            phoneListener.invoke("phone ${blackNumber.blackNumber}")
            breakCall(context)
        } else if (telephony.callState == TelephonyManager.CALL_STATE_IDLE) {
            Executors.newSingleThreadScheduledExecutor().schedule({
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