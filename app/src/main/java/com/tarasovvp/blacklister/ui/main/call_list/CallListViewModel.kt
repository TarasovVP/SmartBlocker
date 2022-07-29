package com.tarasovvp.blacklister.ui.main.call_list

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.LogCall
import com.tarasovvp.blacklister.repository.BlockedCallRepository
import com.tarasovvp.blacklister.repository.LogCallRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class CallListViewModel(application: Application) : BaseViewModel(application) {

    private val callRepository = LogCallRepository
    private val blockedCallRepository = BlockedCallRepository

    val logCallLiveData = MutableLiveData<List<LogCall>>()
    val blockedCallLiveData = MutableLiveData<List<BlockedCall>>()
    val callHashMapLiveData = MutableLiveData<HashMap<String, List<Call>>?>()

    fun getBlockedCallList() {
        showProgress()
        launch {
             val blockedCallList = blockedCallRepository.allBlockedCalls()
            Log.e("callLogTAG", "CallListViewModel getBlockedCallList blockedCallList $blockedCallList")
             blockedCallList?.apply {
                 blockedCallLiveData.postValue(this)
             }
             hideProgress()
        }
    }

    fun getLogCallList() {
        showProgress()
        launch {
            val allLogCalls = callRepository.getAllLogCalls()
            allLogCalls?.apply {
                logCallLiveData.postValue(this)
            }
            hideProgress()
        }
    }

    fun getHashMapFromCallList(callList: List<Call>) {
        Log.e("callLogTAG",
            "CallLogListViewModel getHashMapFromCallLogList callLogList.size ${callList.size}")
        showProgress()
        launch {
            val hashMapList =
                callRepository.getHashMapFromCallList(callList.sortedByDescending {
                    it.time
                })
            Log.e("callLogTAG",
                "CallLogListViewModel getHashMapFromCallLogList hashMapList.size ${hashMapList.size}")
            callHashMapLiveData.postValue(hashMapList)
            hideProgress()
        }
    }
}