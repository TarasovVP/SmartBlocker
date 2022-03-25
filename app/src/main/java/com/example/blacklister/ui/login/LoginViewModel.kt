package com.example.blacklister.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.blacklister.extensions.callLogList
import com.example.blacklister.extensions.contactList
import com.example.blacklister.extensions.toFormattedPhoneNumber
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.provider.BlackNumberRepositoryImpl
import com.example.blacklister.provider.CallLogRepositoryImpl
import com.example.blacklister.provider.ContactRepositoryImpl
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val callLogRepository = CallLogRepositoryImpl
    private val blackNumberRepository = BlackNumberRepositoryImpl
    private val contactRepository = ContactRepositoryImpl

    val successLiveData = MutableLiveData<Boolean>()

    fun getCallLogList() {
        viewModelScope.launch {
            try {
                Log.e("loginViewModelTAG", "LoginViewModel getCallLogList viewModelScope.launch")
                val blackNumberList = blackNumberRepository.allBlackNumbers()
                Log.e(
                    "loginViewModelTAG",
                    "LoginViewModel getCallLogList blackNumberRepository.allBlackNumbers blackNumberList?.size ${blackNumberList?.size}"
                )
                val contactList = getApplication<Application>().contactList()
                contactList.forEach { callLog ->
                    callLog.isBlackList = blackNumberList?.contains(
                        callLog.phone?.toFormattedPhoneNumber()
                            ?.let { phone -> BlackNumber(phone) }) == true
                }
                contactRepository.insertContacts(contactList)
                Log.e(
                    "loginViewModelTAG",
                    "LoginViewModel getCallLogList  contactRepository.insertContacts contactList?.size ${contactList.size}"
                )
                val callLogList = getApplication<Application>().callLogList()
                callLogList.forEach { callLog ->
                    callLog.isBlackList = blackNumberList?.contains(
                        callLog.phone?.toFormattedPhoneNumber()
                            ?.let { phone -> BlackNumber(phone) }) == true
                    val index = contactList.indexOfFirst { it.phone == callLog.phone }
                    if (index >= 0) {
                        val contact =
                            contactList[contactList.indexOfFirst { it.phone == callLog.phone }]
                        callLog.photoUrl = contact.photoUrl
                    }
                }
                callLogRepository.insertCallLogs(callLogList)
                Log.e(
                    "loginViewModelTAG",
                    "LoginViewModel getCallLogList callLogRepository.insertCallLogs callLogList?.size ${callLogList.size}"
                )
                successLiveData.postValue(true)
            } catch (e: Exception) {
                successLiveData.postValue(false)
                e.printStackTrace()
            }
        }
    }
}