package com.tarasovvp.smartblocker.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.constants.Constants.CALL_RECEIVE
import com.tarasovvp.smartblocker.constants.Constants.SECOND
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.SharedPrefs
import com.tarasovvp.smartblocker.database.entities.Filter
import com.tarasovvp.smartblocker.repository.FilterRepository
import com.tarasovvp.smartblocker.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.utils.PermissionUtil.checkPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
open class CallReceiver(private val phoneListener: () -> Unit) : BroadcastReceiver() {

    @Inject
    lateinit var filterRepository: FilterRepository

    @Inject
    lateinit var filteredCallRepository: FilteredCallRepository

    init {
        CoroutineScope(Dispatchers.IO).launch {
            SmartBlockerApp.instance?.apply {
                phoneListener.invoke()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (!context.checkPermissions() || !intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) return
        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER).orEmpty()
        CoroutineScope(Dispatchers.IO).launch {
            val filter = if (number.isEmpty() && SharedPrefs.blockHidden.isTrue()) {
                Filter(filterType = BLOCKER)
            } else {
                filterRepository.queryFilter(number)?.filter
            }
            if ((filter?.isBlocker()
                    .isTrue()) && telephony.callState == TelephonyManager.CALL_STATE_RINGING
            ) {
                breakCall(context)
            } else if (telephony.callState == TelephonyManager.CALL_STATE_IDLE) {
                delay(SECOND)
                if (filter.isNotNull()) {
                    context.writeFilteredCall(number, filter, filteredCallRepository)
                    phoneListener.invoke()
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