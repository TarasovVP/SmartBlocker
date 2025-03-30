package com.tarasovvp.smartblocker.infrastructure.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import com.tarasovvp.smartblocker.domain.entities.dbentities.FilteredCall
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CALL_RECEIVE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.EXTRA_INCOMING_NUMBER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.SECOND
import com.tarasovvp.smartblocker.utils.PermissionUtil.checkPermissions
import com.tarasovvp.smartblocker.utils.extensions.breakCallNougatAndLower
import com.tarasovvp.smartblocker.utils.extensions.breakCallPieAndHigher
import com.tarasovvp.smartblocker.utils.extensions.createFilteredCall
import com.tarasovvp.smartblocker.utils.extensions.isAuthorisedUser
import com.tarasovvp.smartblocker.utils.extensions.isCallStateIdle
import com.tarasovvp.smartblocker.utils.extensions.isCallStateRinging
import com.tarasovvp.smartblocker.utils.extensions.isNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
open class CallReceiver : BroadcastReceiver() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var filterRepository: FilterRepository

    @Inject
    lateinit var realDataBaseRepository: RealDataBaseRepository

    @Inject
    lateinit var filteredCallRepository: FilteredCallRepository

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (context.checkPermissions().not() || intent.hasExtra(EXTRA_INCOMING_NUMBER).not()) return
        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val number = intent.getStringExtra(EXTRA_INCOMING_NUMBER).orEmpty()
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreRepository.blockHidden().collect { isBlockHidden ->
                val matchedFilter = matchedFilter(number, isBlockHidden)
                matchedFilter?.let { filter ->
                    if (filter.filterType == BLOCKER && telephony.isCallStateRinging()) {
                        breakCall(context)
                    } else if (telephony.isCallStateIdle()) {
                        delay(SECOND)
                        context.createFilteredCall(number, filter)?.let { filteredCall ->
                            insertFilteredCall(filteredCall)
                        }
                        context.sendBroadcast(Intent(CALL_RECEIVE))
                    }
                }
                if (telephony.isCallStateIdle() && matchedFilter.isNull()) {
                    context.sendBroadcast(
                        Intent(CALL_RECEIVE),
                    )
                }
            }
        }
    }

    private suspend fun insertFilteredCall(filteredCall: FilteredCall) {
        if (firebaseAuth.isAuthorisedUser()) {
            realDataBaseRepository.insertFilteredCall(filteredCall) {
                runBlocking {
                    filteredCallRepository.insertFilteredCall(filteredCall)
                }
            }
        } else {
            filteredCallRepository.insertFilteredCall(filteredCall)
        }
    }

    suspend fun matchedFilter(
        number: String,
        isBlockHidden: Boolean?,
    ): Filter? {
        return if (number.isEmpty() && isBlockHidden.isTrue()) {
            Filter(filterType = BLOCKER)
        } else {
            filterRepository.allFilterWithFilteredNumbersByNumber(number).firstOrNull()?.filter
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
