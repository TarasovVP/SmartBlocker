package com.tarasovvp.blacklister.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants.CALL_RECEIVE
import com.tarasovvp.blacklister.extensions.breakCallNougatAndLower
import com.tarasovvp.blacklister.extensions.breakCallPieAndHigher
import com.tarasovvp.blacklister.extensions.deleteLastMissedCall
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.provider.BlackNumberRepositoryImpl
import com.tarasovvp.blacklister.provider.WhiteNumberRepositoryImpl
import com.tarasovvp.blacklister.utils.PermissionUtil.checkPermissions
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

open class CallReceiver(private val phoneListener: (String) -> Unit) : BroadcastReceiver() {

    private val blackNumberRepository = BlackNumberRepositoryImpl
    private val whiteNumberRepository = WhiteNumberRepositoryImpl

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        if (!context.checkPermissions() || !intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) return
        Log.e("callReceiveTAG", "CallReceiver onReceive intent.extras ${intent.extras}")
        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val phone = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER).orEmpty()
        val blackNumberList = blackNumberRepository.getBlackNumberList(phone)
        val whiteNumberList = whiteNumberRepository.getWhiteNumberList(phone)
        if (blackNumberList?.isNullOrEmpty()?.not().isTrue() && whiteNumberList?.isNullOrEmpty().isTrue() && telephony.callState == TelephonyManager.CALL_STATE_RINGING) {
            phoneListener.invoke("phone $phone")
            breakCall(context)
            Log.e("callReceiveTAG",
                "CallReceiver blackNumberList?.isNullOrEmpty()?.not().isTrue() phone $phone")
        } else if (telephony.callState == TelephonyManager.CALL_STATE_IDLE && phone.isNotEmpty()) {
            Executors.newSingleThreadScheduledExecutor().schedule({
                if (blackNumberList?.isNullOrEmpty()?.not().isTrue()) {
                    val isDeleteSuccess = context.deleteLastMissedCall(phone)
                    Log.e("callReceiveTAG",
                        "CallReceiver (blackNumberList?.isNullOrEmpty()?.not().isTrue() isDeleteSuccess $isDeleteSuccess")
                }
                context.sendBroadcast(Intent(CALL_RECEIVE))
                Log.e("callReceiveTAG",
                    "CallReceiver sendBroadcast(Intent(CALL_RECEIVE)) phone $phone")
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