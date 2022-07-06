package com.tarasovvp.blacklister.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tarasovvp.blacklister.extensions.callLogList
import com.tarasovvp.blacklister.extensions.contactList
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.CallLog
import com.tarasovvp.blacklister.repository.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val callLogRepository = CallLogRepository
    private val blackNumberRepository = BlackNumberRepository
    private val whiteNumberRepository = WhiteNumberRepository
    private val contactRepository = ContactRepository
    private val blockedCallRepository = BlockedCallRepository
    private val realDataBaseRepository = RealDataBaseRepository

    val successWhiteNumberLiveData = MutableLiveData<Boolean>()
    val successAllDataLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()

    fun getWhiteListPriority() {
        viewModelScope.launch {
            try {
                realDataBaseRepository.getWhiteListPriority { isWhiteListPriority ->
                    SharedPreferencesUtil.isWhiteListPriority = isWhiteListPriority
                    insertAllBlackNumbers()
                }
            } catch (e: Exception) {
                errorLiveData.postValue(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }

    fun insertAllBlackNumbers() {
        viewModelScope.launch {
            try {
                blackNumberRepository.insertAllBlackNumbers {
                    insertAllWhiteNumbers()
                }
            } catch (e: Exception) {
                errorLiveData.postValue(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }

    fun insertAllWhiteNumbers() {
        viewModelScope.launch {
            try {
                whiteNumberRepository.insertAllWhiteNumbers {
                    successWhiteNumberLiveData.postValue(true)
                }
            } catch (e: java.lang.Exception) {
                errorLiveData.postValue(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }

    fun getAllData() {
        viewModelScope.launch {
            try {
                val contactList = getApplication<Application>().contactList()
                contactList.forEach { contact ->
                    contact.isBlackList =
                        blackNumberRepository.getBlackNumberList(contact.phone.orEmpty())
                            ?.isNullOrEmpty().isTrue().not()
                    contact.isWhiteList =
                        whiteNumberRepository.getWhiteNumberList(contact.phone.orEmpty())
                            ?.isNullOrEmpty().isTrue().not()
                }
                contactRepository.insertContacts(contactList)

                val callLogList = getApplication<Application>().callLogList()
                val blockedCallList = blockedCallRepository.allBlockedCalls()
                blockedCallList?.forEach { blockedCall ->
                    callLogList.add(CallLog(name = blockedCall.name,
                        phone = blockedCall.phone,
                        type = blockedCall.type,
                        time = blockedCall.time))
                }
                callLogList.forEach { callLog ->
                    callLog.isBlackList =
                        blackNumberRepository.getBlackNumberList(callLog.phone.orEmpty())
                            ?.isNullOrEmpty().isTrue().not()
                    callLog.isWhiteList =
                        whiteNumberRepository.getWhiteNumberList(callLog.phone.orEmpty())
                            ?.isNullOrEmpty().isTrue().not()
                    val index = contactList.indexOfFirst { it.phone == callLog.phone }
                    if (index >= 0) {
                        val contact =
                            contactList[contactList.indexOfFirst { it.phone == callLog.phone }]
                        callLog.photoUrl = contact.photoUrl
                    }
                }
                callLogRepository.insertCallLogs(callLogList)

                successAllDataLiveData.postValue(true)
            } catch (e: Exception) {
                errorLiveData.postValue(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }
}