package com.tarasovvp.smartblocker.ui.main.number.list.list_call

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.repository.CallRepository
import com.tarasovvp.smartblocker.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import kotlinx.coroutines.async

class ListCallViewModel(application: Application) : BaseViewModel(application) {

    private val callRepository = CallRepository
    private val filteredCallRepository = FilteredCallRepository

    val callListLiveData = MutableLiveData<List<Call>>()
    val callHashMapLiveData = MutableLiveData<Map<String, List<Call>>?>()
    val successDeleteNumberLiveData = MutableLiveData<Boolean>()

    fun getCallList(refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val logCalls = async { callRepository.getAllLogCalls() }
            val filteredCalls = async { filteredCallRepository.allFilteredCalls() }
            val filteredCallList = filteredCalls.await().orEmpty()
            val logCallList = logCalls.await().orEmpty()
            val callList = ArrayList<Call>().apply {
                addAll(filteredCallList)
                addAll(logCallList)
            }
            callListLiveData.postValue(callList.distinctBy {
                it.callId
            })
            hideProgress()
        }
    }

    fun getHashMapFromCallList(callList: List<Call>, refreshing: Boolean) {
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
            filteredCallRepository.deleteFilteredCalls(callList) {
                successDeleteNumberLiveData.postValue(true)
                hideProgress()
            }
        }
    }
}