package com.tarasovvp.smartblocker.infrastructure.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CALL_RECEIVE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.SECOND
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.utils.PermissionUtil.checkPermissions
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
open class CallReceiver : BroadcastReceiver() {

    @Inject
    lateinit var filterRepository: FilterRepository

    @Inject
    lateinit var filteredCallRepository: FilteredCallRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (context.checkPermissions().not() || intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER).not()) return
        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER).orEmpty()
        CoroutineScope(Dispatchers.IO).launch {
            matchedFilter(number)?.let { filter ->
                if (filter.isBlocker() && telephony.callState == TelephonyManager.CALL_STATE_RINGING) {
                    breakCall(context)
                } else if (telephony.callState == TelephonyManager.CALL_STATE_IDLE) {
                    delay(SECOND)
                    val test = context.createFilteredCall(number, filter)
                    test?.
                    let { filteredCallRepository.insertFilteredCall(it) }
                    context.sendBroadcast(Intent(CALL_RECEIVE))
                }
            }
        }
    }

    private suspend fun matchedFilter(number: String): Filter? {
        return if (number.isEmpty() && SharedPrefs.blockHidden.isTrue()) {
            Filter(filterType = BLOCKER)
        } else {
            filterRepository.allFilterWithCountryCodesByNumber(number).firstOrNull()?.filter
        }
    }

    private fun breakCall(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.breakCallPieAndHigher()
        } else {
            context.breakCallNougatAndLower()
        }
    }
}