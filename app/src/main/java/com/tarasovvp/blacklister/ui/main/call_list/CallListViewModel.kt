package com.tarasovvp.blacklister.ui.main.call_list

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.repository.BlockedCallRepository
import com.tarasovvp.blacklister.repository.CallRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class CallListViewModel(application: Application) : BaseViewModel(application) {

    private val callRepository = CallRepository
    private val blockedCallRepository = BlockedCallRepository

    val callLiveData = MutableLiveData<List<Call>>()
    val callHashMapLiveData = MutableLiveData<Map<String, List<Call>>?>()
    val successDeleteNumberLiveData = MutableLiveData<Boolean>()

    fun getBlockedCallList() {
        Log.e("callTAG", "CallListViewModel getBlockedCallList start")
        showProgress()
        launch {
            val blockedCallList = blockedCallRepository.allBlockedCalls()
            Log.e("callTAG", "CallListViewModel getBlockedCallList allBlockedCalls() size ${blockedCallList?.size}")
            blockedCallList?.apply {
                callLiveData.postValue(this)
            }
            hideProgress()
        }
    }

    fun getLogCallList() {
        showProgress()
        launch {
            Log.e("callTAG", "CallListViewModel getLogCallList() start")
            val allLogCalls = callRepository.getAllLogCalls()
            Log.e("callTAG", "CallListViewModel getLogCallList() getAllLogCalls size ${allLogCalls?.size}")
            allLogCalls?.apply {
                callLiveData.postValue(this)
            }
            hideProgress()
        }
    }

    fun getHashMapFromCallList(callList: List<Call>) {
        Log.e("callTAG", "CallListViewModel getHashMapFromCallList() start")
        showProgress()
        launch {
            val hashMapList =
                callRepository.getHashMapFromCallList(callList.sortedByDescending {
                    it.time
                })
            Log.e("callTAG", "CallListViewModel getHashMapFromCallList() getHashMapFromCallList hashMapList $hashMapList")
            callHashMapLiveData.postValue(hashMapList)
            hideProgress()
        }
    }

    fun deleteCallList(callList: List<Call>) {
        showProgress()
        launch {
            blockedCallRepository.deleteBlockedCalls(callList)
            successDeleteNumberLiveData.postValue(true)
            hideProgress()
        }
    }
}
