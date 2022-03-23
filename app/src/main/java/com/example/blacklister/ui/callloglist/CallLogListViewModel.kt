package com.example.blacklister.ui.callloglist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.blacklister.extensions.callLogList
import com.example.blacklister.extensions.toFormattedPhoneNumber
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.model.CallLog
import com.example.blacklister.provider.BlackNumberRepositoryImpl
import com.example.blacklister.provider.CallLogRepositoryImpl
import kotlinx.coroutines.launch

class CallLogListViewModel(application: Application) : AndroidViewModel(application) {

    private val callLogRepository = CallLogRepositoryImpl
    private val blackNumberRepository = BlackNumberRepositoryImpl

    val callLogLiveData = MutableLiveData<List<CallLog>>()
    val callLogHashMapLiveData = MutableLiveData<HashMap<String, List<CallLog>>?>()

    fun getCallLogList() {
        viewModelScope.launch {
            val blackNumberList = blackNumberRepository.allBlackNumbers()
            val callLogList = getApplication<Application>().callLogList()
            callLogList.forEach { callLog ->
                callLog.isBlackList =
                    blackNumberList?.contains(
                        callLog.phone?.toFormattedPhoneNumber()
                            ?.let { phone -> BlackNumber(phone) }) == true
            }
            callLogRepository.insertCallLogs(callLogList)
            val allCallLogs = callLogRepository.getAllCallLogs()
            allCallLogs?.apply {
                callLogLiveData.postValue(this)
            }
        }
    }

    fun getHashMapFromCallLogList(callLogList: List<CallLog>) {
        viewModelScope.launch {
            callLogHashMapLiveData.postValue(callLogRepository.getHashMapFromCallLogList(callLogList))
        }
    }
}