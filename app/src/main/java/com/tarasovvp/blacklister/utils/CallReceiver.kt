package com.tarasovvp.blacklister.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.CALL_RECEIVE
import com.tarasovvp.blacklister.extensions.breakCallNougatAndLower
import com.tarasovvp.blacklister.extensions.breakCallPieAndHigher
import com.tarasovvp.blacklister.extensions.deleteLastBlockedCall
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.repository.BlockedCallRepository
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.utils.PermissionUtil.checkPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class CallReceiver(private val phoneListener: (String) -> Unit) : BroadcastReceiver() {

    private val filterRepository = FilterRepository
    private val blockedCallRepository = BlockedCallRepository

    init {
        CoroutineScope(Dispatchers.IO).launch {
            BlackListerApp.instance?.apply {
                phoneListener.invoke(String.format(this.getString(R.string.blocked_calls),
                    blockedCallRepository.allBlockedCalls()?.size))
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
            val filterList = filterRepository.getFilterList(number)
            val isInWhiteList =
                filterList?.any { it.isBlackFilter().not() }.isTrue()
            val isInBlackList =
                filterList?.any { it.isBlackFilter() }.isTrue()
            val isBlockNeeded =
                (isInBlackList ) || (isInBlackList && isInWhiteList.not()) || (number.isEmpty() && SharedPreferencesUtil.blockHidden)
            Log.e("blockTAG",
                "CallReceiver onReceive telephony.callState ${telephony.callState} phone $number isBlockNeeded $isBlockNeeded blockHidden ${SharedPreferencesUtil.blockHidden}")
            if (isBlockNeeded && telephony.callState == TelephonyManager.CALL_STATE_RINGING) {
                Log.e("blockTAG", "CallReceiver onReceive breakCall")
                breakCall(context)
            } else if (telephony.callState == TelephonyManager.CALL_STATE_IDLE) {
                Log.e("blockTAG",
                    "CallReceiver onReceive phone $number currentTimeMillis ${System.currentTimeMillis()}")
                delay(2000)
                if (isBlockNeeded) {
                    Log.e("blockTAG",
                        "CallReceiver newSingleThreadScheduledExecutor phone $number currentTimeMillis ${System.currentTimeMillis()}")
                    context.deleteLastBlockedCall(number)
                    phoneListener.invoke(String.format(context.getString(R.string.blocked_calls),
                        blockedCallRepository.allBlockedCalls()?.size))
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