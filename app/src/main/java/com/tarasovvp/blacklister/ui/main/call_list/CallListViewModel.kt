package com.tarasovvp.blacklister.ui.main.call_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.repository.BlockedCallRepository
import com.tarasovvp.blacklister.repository.LogCallRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class CallListViewModel(application: Application) : BaseViewModel(application) {

    private val callRepository = LogCallRepository
    private val blockedCallRepository = BlockedCallRepository

    val callLiveData = MutableLiveData<List<Call>>()
    val callHashMapLiveData = MutableLiveData<HashMap<String, List<Call>>?>()
    val successDeleteNumberLiveData = MutableLiveData<Boolean>()

    fun getBlockedCallList() {
        showProgress()
        launch {
            val blockedCallList = blockedCallRepository.allBlockedCalls()
            blockedCallList?.apply {
                callLiveData.postValue(this)
            }
            hideProgress()
        }
    }

    fun getLogCallList() {
        showProgress()
        launch {
            val allLogCalls = callRepository.getAllLogCalls()
            allLogCalls?.apply {
                callLiveData.postValue(this)
            }
            hideProgress()
        }
    }

    fun getHashMapFromCallList(callList: List<Call>) {
        showProgress()
        launch {
            val hashMapList =
                callRepository.getHashMapFromCallList(callList.sortedByDescending {
                    it.time
                })
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