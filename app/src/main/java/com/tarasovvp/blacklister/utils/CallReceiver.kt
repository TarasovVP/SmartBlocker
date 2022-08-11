package com.tarasovvp.blacklister.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.CALL_RECEIVE
import com.tarasovvp.blacklister.extensions.breakCallNougatAndLower
import com.tarasovvp.blacklister.extensions.breakCallPieAndHigher
import com.tarasovvp.blacklister.extensions.deleteLastMissedCall
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.repository.BlackFilterRepository
import com.tarasovvp.blacklister.repository.BlockedCallRepository
import com.tarasovvp.blacklister.repository.WhiteFilterRepository
import com.tarasovvp.blacklister.utils.PermissionUtil.checkPermissions
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

open class CallReceiver(private val phoneListener: (String) -> Unit) : BroadcastReceiver() {

    private val blackFilterRepository = BlackFilterRepository
    private val whiteFilterRepository = WhiteFilterRepository
    private val blockedCallRepository = BlockedCallRepository

    init {
        BlackListerApp.instance?.apply {
            phoneListener.invoke(String.format(this.getString(R.string.blocked_calls),
                blockedCallRepository.allBlockedCalls()?.size))
        }
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        if (!context.checkPermissions() || !intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) return
        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val phone = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER).orEmpty()
        val isInWhiteList =
            whiteFilterRepository.getWhiteFilterList(phone)?.isEmpty().isTrue().not()
        val isInBlackList =
            blackFilterRepository.getBlackFilterList(phone)?.isEmpty().isTrue().not()
        val isBlockNeeded =
            (isInBlackList && SharedPreferencesUtil.isWhiteListPriority.not()) || (isInBlackList && SharedPreferencesUtil.isWhiteListPriority && isInWhiteList.not()) || (phone.isEmpty() && SharedPreferencesUtil.blockHidden)
        if (isBlockNeeded && telephony.callState == TelephonyManager.CALL_STATE_RINGING) {
            breakCall(context)
        } else if (telephony.callState == TelephonyManager.CALL_STATE_IDLE && phone.isNotEmpty()) {
            Executors.newSingleThreadScheduledExecutor().schedule({
                if (isBlockNeeded) {
                    context.deleteLastMissedCall(phone)
                    phoneListener.invoke(String.format(context.getString(R.string.blocked_calls),
                        blockedCallRepository.allBlockedCalls()?.size))
                }
                context.sendBroadcast(Intent(CALL_RECEIVE))
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