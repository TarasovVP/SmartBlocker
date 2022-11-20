package com.tarasovvp.smartblocker.ui.number_data.call_list

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.model.Call
import com.tarasovvp.smartblocker.repository.BlockedCallRepository
import com.tarasovvp.smartblocker.repository.CallRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class CallListViewModel(application: Application) : BaseViewModel(application) {

    private val callRepository = CallRepository
    private val blockedCallRepository = BlockedCallRepository

    val callListLiveData = MutableLiveData<List<Call>>()
    val callHashMapLiveData = MutableLiveData<Map<String, List<Call>>?>()
    val successDeleteNumberLiveData = MutableLiveData<Boolean>()

    fun getCallList(refreshing: Boolean) {
        Log.e("adapterTAG",
            "CallListViewModel getCallList refreshing $refreshing")
        if (refreshing.not()) showProgress()
        launch {
            val logCalls = async { callRepository.getAllLogCalls() }
            val blockedCalls = async { blockedCallRepository.allBlockedCalls() }
            awaitAll(logCalls, blockedCalls)
            val logCallList = logCalls.await().orEmpty()
            val blockedCallList = blockedCalls.await().orEmpty()
            val callList = ArrayList<Call>().apply {
                addAll(logCallList)
                addAll(blockedCallList)
            }
            callListLiveData.postValue(callList)
            hideProgress()
        }
    }

    fun getHashMapFromCallList(callList: List<Call>, refreshing: Boolean) {
        Log.e("adapterTAG",
            "CallListViewModel getHashMapFromFilterList refreshing $refreshing")
        if (refreshing.not()) showProgress()
        launch {
            val hashMapList =
                callRepository.getHashMapFromCallList(callList.sortedByDescending {
                    it.callDate
                })
            callHashMapLiveData.postValue(hashMapList)
            hideProgress()
        }
    }

    fun deleteCallList(callList: List<Call>) {
        showProgress()
        launch {
            blockedCallRepository.deleteBlockedCalls(callList) {
                successDeleteNumberLiveData.postValue(true)
                hideProgress()
            }
        }
    }
}
