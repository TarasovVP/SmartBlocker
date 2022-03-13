package com.example.blacklister.ui.callloglist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.blacklister.extensions.callLogList
import com.example.blacklister.extensions.formattedPhoneNumber
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.provider.BlackNumberRepositoryImpl
import com.example.blacklister.provider.CallLogRepositoryImpl
import kotlinx.coroutines.launch

class CallLogListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CallLogRepositoryImpl
    private val blackNumberRepository = BlackNumberRepositoryImpl

    val callLogLiveData = repository.subscribeToCallLogs()

    fun getCallLogList() {
        viewModelScope.launch {
            val blackNumberList = blackNumberRepository.allBlackNumbers()
            val callLogList = getApplication<Application>().callLogList()
            callLogList.forEach { callLog ->
                callLog.isBlackList =
                    blackNumberList?.contains(BlackNumber(callLog.phone.formattedPhoneNumber())) == true
            }
            repository.insertCallLogs(callLogList)
        }
    }
}