package com.tarasovvp.blacklister.ui.number_data.call_list

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.repository.BlockedCallRepository
import com.tarasovvp.blacklister.repository.CallRepository
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class CallListViewModel(application: Application) : BaseViewModel(application) {

    private val callRepository = CallRepository
    private val blockedCallRepository = BlockedCallRepository
    private val filterRepository = FilterRepository

    val callListLiveData = MutableLiveData<List<Call>>()
    val callHashMapLiveData = MutableLiveData<Map<String, List<Call>>?>()
    val successDeleteNumberLiveData = MutableLiveData<Boolean>()

    fun getCallList() {
        launch {
            val logCalls = async { callRepository.getAllLogCalls() }
            val blockedCalls = async { blockedCallRepository.allBlockedCalls() }
            awaitAll(logCalls, blockedCalls)
            val logCallList = logCalls.await().orEmpty()
            val blockedCallList = blockedCalls.await().orEmpty()
            blockedCallList.forEach { logCall ->
                logCall.filterType = filterRepository.queryFilterList(logCall.number)
                    ?.firstOrNull()?.filterType ?: Constants.DEFAULT_FILTER
            }
            val callList = ArrayList<Call>().apply {
                addAll(logCallList)
                addAll(blockedCallList)
            }
            callListLiveData.postValue(callList)
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
            Log.e("callTAG",
                "CallListViewModel getHashMapFromCallList() getHashMapFromCallList hashMapList $hashMapList")
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
