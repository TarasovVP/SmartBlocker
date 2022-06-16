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
import com.tarasovvp.blacklister.model.CallLog
import com.tarasovvp.blacklister.provider.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val callLogRepository = CallLogRepositoryImpl
    private val blackNumberRepository = BlackNumberRepositoryImpl
    private val whiteNumberRepository = WhiteNumberRepositoryImpl
    private val contactRepository = ContactRepositoryImpl
    private val blockedCallRepository = BlockedCallRepositoryImpl

    val successBlackNumberLiveData = MutableLiveData<Boolean>()
    val successWhiteNumberLiveData = MutableLiveData<Boolean>()
    val successAllDataLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()

    fun insertAllBlackNumbers() {
        viewModelScope.launch {
            try {
                blackNumberRepository.insertAllBlackNumbers {
                    successBlackNumberLiveData.postValue(true)
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
                Log.e("mainViewModelTAG",
                    "MainViewModel getAllData contactList ${Gson().toJson(contactList)}")

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
                Log.e("mainViewModelTAG",
                    "MainViewModel getAllData callLogList ${Gson().toJson(callLogList)}")

                successAllDataLiveData.postValue(true)
            } catch (e: Exception) {
                errorLiveData.postValue(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }
}