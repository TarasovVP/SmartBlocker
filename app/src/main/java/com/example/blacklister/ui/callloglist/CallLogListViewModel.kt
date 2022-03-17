package com.example.blacklister.ui.callloglist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.blacklister.extensions.callLogList
import com.example.blacklister.extensions.formattedPhoneNumber
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.provider.BlackNumberRepositoryImpl
import com.example.blacklister.provider.CallLogRepositoryImpl
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CallLogListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CallLogRepositoryImpl
    private val blackNumberRepository = BlackNumberRepositoryImpl

    val callLogLiveData = repository.subscribeToCallLogs()

    fun getCallLogList() {
        Log.e("callLogTAG", "CallLogListViewModel getCallLogList repository.getAllCallLogs() ${Gson().toJson(repository.getAllCallLogs())}")
        viewModelScope.launch {
            val blackNumberList = blackNumberRepository.allBlackNumbers()
            val callLogList = getApplication<Application>().callLogList()
            callLogList.forEach { callLog ->
                callLog.isBlackList =
                    blackNumberList?.contains(callLog.phone?.formattedPhoneNumber()?.let { phone -> BlackNumber(phone) }) == true
            }
            repository.insertCallLogs(callLogList)
            Log.e("callLogTAG", "CallLogListViewModel viewModelScope.launch repository.getAllCallLogs() ${Gson().toJson(repository.getAllCallLogs())}")
        }
    }
}