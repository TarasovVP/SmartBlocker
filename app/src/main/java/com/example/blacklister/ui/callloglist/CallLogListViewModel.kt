package com.example.blacklister.ui.callloglist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.blacklister.extensions.toMillisecondsFromString
import com.example.blacklister.model.CallLog
import com.example.blacklister.provider.CallLogRepositoryImpl
import kotlinx.coroutines.launch

class CallLogListViewModel(application: Application) : AndroidViewModel(application) {

    private val callLogRepository = CallLogRepositoryImpl

    val callLogLiveData = MutableLiveData<List<CallLog>>()
    val callLogHashMapLiveData = MutableLiveData<HashMap<String, List<CallLog>>?>()

    fun getCallLogList() {
        viewModelScope.launch {
            val allCallLogs = callLogRepository.getAllCallLogs()
            /*allCallLogs?.sortedByDescending {
                it.time?.toMillisecondsFromString()
            }*/
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