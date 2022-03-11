package com.example.blacklister.ui.callloglist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.blacklister.extensions.callLogList
import com.example.blacklister.provider.CallLogRepositoryImpl
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CallLogListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CallLogRepositoryImpl

    val callLogLiveData = repository.subscribeToCallLogs()

    fun getCallLogList() {
        viewModelScope.launch {
            val callLogList = getApplication<Application>().callLogList()
            Log.e("callTAG", "callLogList ${Gson().toJson(callLogList)}")
            repository.insertCallLogs(callLogList)
        }
    }
}