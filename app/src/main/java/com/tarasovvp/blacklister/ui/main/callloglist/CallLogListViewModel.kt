package com.tarasovvp.blacklister.ui.main.callloglist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.CallLog
import com.tarasovvp.blacklister.repository.CallLogRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class CallLogListViewModel(application: Application) : BaseViewModel(application) {

    private val callLogRepository = CallLogRepository

    val callLogLiveData = MutableLiveData<List<CallLog>>()
    val callLogHashMapLiveData = MutableLiveData<HashMap<String, List<CallLog>>?>()

    fun getCallLogList() {
        launch {
            val allCallLogs = callLogRepository.getAllCallLogs()
            allCallLogs?.apply {
                callLogLiveData.postValue(this)
            }
        }
    }

    fun getHashMapFromCallLogList(callLogList: List<CallLog>) {
        launch {
            val hashMapList =
                callLogRepository.getHashMapFromCallLogList(callLogList.sortedByDescending {
                    it.time
                })
            callLogHashMapLiveData.postValue(hashMapList)
        }
    }
}