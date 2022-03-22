package com.example.blacklister.ui.callloglist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.blacklister.extensions.callLogList
import com.example.blacklister.extensions.formattedPhoneNumber
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
            Log.e("dataTAG", "CallLogListViewModel getCallLogList allBlackNumbers")
            val callLogList = getApplication<Application>().callLogList()
            Log.e("dataTAG", "CallLogListViewModel getCallLogList callLogList")
            callLogList.forEach { callLog ->
                callLog.isBlackList =
                    blackNumberList?.contains(
                        callLog.phone?.formattedPhoneNumber()
                            ?.let { phone -> BlackNumber(phone) }) == true
            }
            callLogRepository.insertCallLogs(callLogList)
            Log.e("dataTAG", "CallLogListViewModel getCallLogList insertCallLogs")
            val allCallLogs = callLogRepository.getAllCallLogs()
            Log.e("dataTAG", "CallLogListViewModel getCallLogList repository.getAllCallLogs()")
            allCallLogs?.apply {
                callLogLiveData.postValue(this)
            }
            Log.e("dataTAG", "CallLogListViewModel getCallLogList callLogLiveData.postValue(this)")
            /*Log.e(
                "dataTAG",
                "CallLogListViewModel viewModelScope.launch repository.getAllCallLogs() ${
                    Gson().toJson(repository.getAllCallLogs())
                }"
            )*/
        }
    }

    fun getHashMapFromCallLogList(callLogList: List<CallLog>) {
        viewModelScope.launch {
            Log.e("dataTAG", "CallLogListViewModel getHashMapFromCallLogList")
            callLogHashMapLiveData.postValue(callLogRepository.getHashMapFromCallLogList(callLogList))
        }
    }
}