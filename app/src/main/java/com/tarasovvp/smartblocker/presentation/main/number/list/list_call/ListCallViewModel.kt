package com.tarasovvp.smartblocker.presentation.main.number.list.list_call

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.data.database.entities.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class ListCallViewModel @Inject constructor(
    application: Application,
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository
) : BaseViewModel(application) {

    val callListLiveData = MutableLiveData<List<CallWithFilter>>()
    val callHashMapLiveData = MutableLiveData<Map<String, List<CallWithFilter>>?>()
    val successDeleteNumberLiveData = MutableLiveData<Boolean>()

    fun getCallList(refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val logCalls = async { logCallRepository.getAllLogCallWithFilter() }
            val filteredCalls = async { filteredCallRepository.allFilteredCallWithFilter() }
            val filteredCallList = filteredCalls.await()
            val logCallList = logCalls.await()
            val callList = ArrayList<CallWithFilter>().apply {
                addAll(filteredCallList)
                addAll(logCallList)
            }
            callListLiveData.postValue(callList.distinctBy {
                it.call?.callId
            })
            hideProgress()
        }
    }

    fun getHashMapFromCallList(callList: List<CallWithFilter>, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val hashMapList =
                logCallRepository.getHashMapFromCallList(callList.sortedByDescending {
                    it.call?.callDate
                })
            callHashMapLiveData.postValue(hashMapList)
            hideProgress()
        }
    }

    fun deleteCallList(filteredCallIdList: List<Int>) {
        showProgress()
        launch {
            filteredCallRepository.deleteFilteredCalls(filteredCallIdList) {
                successDeleteNumberLiveData.postValue(true)
                hideProgress()
            }
        }
    }
}
