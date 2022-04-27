package com.tarasovvp.blacklister.ui.main.callloglist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.CallLog
import com.tarasovvp.blacklister.provider.CallLogRepositoryImpl
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class CallLogListViewModel(application: Application) : BaseViewModel(application) {

    private val callLogRepository = CallLogRepositoryImpl

    val callLogLiveData = MutableLiveData<List<CallLog>>()
    val callLogHashMapLiveData = MutableLiveData<HashMap<String, List<CallLog>>?>()

    fun getCallLogList() {
        viewModelScope.launch {
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