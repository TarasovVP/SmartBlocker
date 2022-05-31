package com.tarasovvp.blacklister.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.extensions.callLogList
import com.tarasovvp.blacklister.extensions.contactList
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.toFormattedPhoneNumber
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.provider.BlackNumberRepositoryImpl
import com.tarasovvp.blacklister.provider.CallLogRepositoryImpl
import com.tarasovvp.blacklister.provider.ContactRepositoryImpl
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val callLogRepository = CallLogRepositoryImpl
    private val blackNumberRepository = BlackNumberRepositoryImpl
    private val contactRepository = ContactRepositoryImpl

    val successLiveData = MutableLiveData<Boolean>()

    fun getAllData() {
        viewModelScope.launch {
            try {
                Log.e("mainViewModelTAG", "MainViewModel getCallLogList viewModelScope.launch")
                val blackNumberList = blackNumberRepository.allBlackNumbers()
                Log.e(
                    "mainViewModelTAG",
                    "MainViewModel getCallLogList blackNumberRepository.allBlackNumbers blackNumberList?.size ${blackNumberList?.size}"
                )
                val contactList = getApplication<Application>().contactList()
                contactList.forEach { callLog ->
                    callLog.isBlackList = blackNumberList?.contains(
                        callLog.phone?.toFormattedPhoneNumber()
                            ?.let { phone -> BlackNumber(phone) }).isTrue()
                }
                contactRepository.insertContacts(contactList)
                Log.e(
                    "mainViewModelTAG",
                    "MainViewModel getCallLogList  contactRepository.insertContacts contactList?.size ${contactList.size}"
                )
                val callLogList = getApplication<Application>().callLogList()
                callLogList.forEach { callLog ->
                    callLog.isBlackList = blackNumberList?.contains(
                        callLog.phone?.toFormattedPhoneNumber()
                            ?.let { phone -> BlackNumber(phone) }).isTrue()
                    val index = contactList.indexOfFirst { it.phone == callLog.phone }
                    if (index >= 0) {
                        val contact =
                            contactList[contactList.indexOfFirst { it.phone == callLog.phone }]
                        callLog.photoUrl = contact.photoUrl
                    }
                }
                callLogRepository.insertCallLogs(callLogList)
                Log.e(
                    "mainViewModelTAG",
                    "MainViewModel getCallLogList callLogRepository.insertCallLogs callLogList?.size ${callLogList.size}"
                )
                successLiveData.postValue(true)
            } catch (e: Exception) {
                successLiveData.postValue(false)
                e.printStackTrace()
            }
        }
    }
}