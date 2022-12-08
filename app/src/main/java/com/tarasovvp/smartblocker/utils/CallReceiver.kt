package com.tarasovvp.smartblocker.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.CALL_RECEIVE
import com.tarasovvp.smartblocker.constants.Constants.SECOND
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.repository.FilterRepository
import com.tarasovvp.smartblocker.utils.PermissionUtil.checkPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class CallReceiver(private val phoneListener: (String) -> Unit) : BroadcastReceiver() {

    private val filterRepository = FilterRepository
    private val filteredCallRepository = FilteredCallRepository

    init {
        CoroutineScope(Dispatchers.IO).launch {
            BlackListerApp.instance?.apply {
                phoneListener.invoke(String.format(this.getString(R.string.blocked_calls),
                    filteredCallRepository.allFilteredCalls()?.size))
            }
        }
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        if (!context.checkPermissions() || !intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) return
        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER).orEmpty()
        Log.e("blockTAG",
            "CallReceiver onReceive telephony.callState ${telephony.callState} phone $number")
        CoroutineScope(Dispatchers.IO).launch {
            val filter = filterRepository.queryFilter(number)
            Log.e("blockTAG",
                "CallReceiver onReceive telephony.callState ${telephony.callState} phone $number isBlockNeeded ${
                    filter?.isBlocker().isTrue()
                } blockHidden ${SharedPreferencesUtil.blockHidden}")
            if (filter?.isBlocker().isTrue() && telephony.callState == TelephonyManager.CALL_STATE_RINGING) {
                Log.e("blockTAG", "CallReceiver onReceive breakCall")
                breakCall(context)
            } else if (telephony.callState == TelephonyManager.CALL_STATE_IDLE) {
                Log.e("blockTAG",
                    "CallReceiver onReceive phone $number currentTimeMillis ${System.currentTimeMillis()}")
                delay(SECOND)
                if (filter.isNotNull()) {
                    Log.e("blockTAG",
                        "CallReceiver newSingleThreadScheduledExecutor phone $number currentTimeMillis ${System.currentTimeMillis()}")
                    context.writeFilteredCall(number, filter)
                    phoneListener.invoke(String.format(context.getString(R.string.blocked_calls),
                        filteredCallRepository.allFilteredCalls()?.size))
                }
                context.sendBroadcast(Intent(CALL_RECEIVE))
            }
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