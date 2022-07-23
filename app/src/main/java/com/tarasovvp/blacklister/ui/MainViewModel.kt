package com.tarasovvp.blacklister.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.CallLog
import com.tarasovvp.blacklister.model.CurrentUser
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.repository.*
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val callLogRepository = CallLogRepository
    private val blackNumberRepository = BlackNumberRepository
    private val whiteNumberRepository = WhiteNumberRepository
    private val contactRepository = ContactRepository
    private val blockedCallRepository = BlockedCallRepository
    private val realDataBaseRepository = RealDataBaseRepository

    val successAllDataLiveData = MutableLiveData<Boolean>()

    fun getCurrentUser() {
        launch {
            showProgress()
            realDataBaseRepository.getCurrentUser { currentUser ->
                SharedPreferencesUtil.isWhiteListPriority =
                    currentUser?.isWhiteListPriority.isTrue()
                currentUser?.let { insertAllBlackNumbers(it) }
            }
        }
    }

    private fun insertAllBlackNumbers(currentUser: CurrentUser) {
        launch {
            blackNumberRepository.insertAllBlackNumbers(currentUser.blackNumberList)
            insertAllWhiteNumbers(currentUser.whiteNumberList)
        }
    }

    private fun insertAllWhiteNumbers(whiteNumberList: ArrayList<WhiteNumber>) {
        launch {
            whiteNumberRepository.insertAllWhiteNumbers(whiteNumberList)
            getAllData()
        }
    }

    fun getAllData() {
        showProgress()
        launch {
            Log.e("allDataTAG", "MainViewModel getAllData")
            val contactList = contactRepository.getSystemContactList(getApplication<Application>())
            contactList.forEach { contact ->
                val isInWhiteList = whiteNumberRepository.getWhiteNumberList(contact.phone.orEmpty())?.isEmpty().isTrue().not()
                val isInBlackList = blackNumberRepository.getBlackNumberList(contact.phone.orEmpty())?.isEmpty().isTrue().not()
                contact.isBlackList = (isInBlackList && SharedPreferencesUtil.isWhiteListPriority.not()) || (isInBlackList && SharedPreferencesUtil.isWhiteListPriority && isInWhiteList.not())
                contact.isWhiteList = (isInWhiteList && SharedPreferencesUtil.isWhiteListPriority) || (isInWhiteList && SharedPreferencesUtil.isWhiteListPriority.not() && isInBlackList.not())
            }
            contactRepository.insertContacts(contactList)

            val callLogList = callLogRepository.getSystemCallLogList(getApplication<Application>())
            val blockedCallList = blockedCallRepository.allBlockedCalls()
            blockedCallList?.forEach { blockedCall ->
                callLogList.add(CallLog(name = blockedCall.name,
                    phone = blockedCall.phone,
                    type = blockedCall.type,
                    time = blockedCall.time))
            }
            callLogList.forEach { callLog ->
                val isInWhiteList = whiteNumberRepository.getWhiteNumberList(callLog.phone.orEmpty())?.isEmpty().isTrue().not()
                val isInBlackList = blackNumberRepository.getBlackNumberList(callLog.phone.orEmpty())?.isEmpty().isTrue().not()
                callLog.isBlackList = (isInBlackList && SharedPreferencesUtil.isWhiteListPriority.not()) || (isInBlackList && SharedPreferencesUtil.isWhiteListPriority && isInWhiteList.not())
                callLog.isWhiteList = (isInWhiteList && SharedPreferencesUtil.isWhiteListPriority) || (isInWhiteList && SharedPreferencesUtil.isWhiteListPriority.not() && isInBlackList.not())
                val index = contactList.indexOfFirst { it.phone == callLog.phone }
                if (index >= 0) {
                    val contact =
                        contactList[contactList.indexOfFirst { it.phone == callLog.phone }]
                    callLog.photoUrl = contact.photoUrl
                }
            }
            callLogRepository.insertCallLogs(callLogList)

            successAllDataLiveData.postValue(true)
            hideProgress()
        }
    }
}