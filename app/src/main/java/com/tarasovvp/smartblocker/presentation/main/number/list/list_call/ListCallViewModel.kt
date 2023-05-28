package com.tarasovvp.smartblocker.presentation.main.number.list.list_call

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.ListCallUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListCallViewModel @Inject constructor(
    private val application: Application,
    private val listCallUseCase: ListCallUseCase,
    private val callWithFilterUIMapper: CallWithFilterUIMapper
) : BaseViewModel(application) {

    val callListLiveData = MutableLiveData<List<CallWithFilterUIModel>>()
    val filteredCallListLiveData = MutableLiveData<List<CallWithFilterUIModel>>()
    val callHashMapLiveData = MutableLiveData<Map<String, List<CallWithFilterUIModel>>?>()
    val successDeleteNumberLiveData = MutableLiveData<Boolean>()

    fun getCallList(refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val callList = listCallUseCase.allCallWithFilters()
            callListLiveData.postValue(callWithFilterUIMapper.mapToUIModelList(callList))
            hideProgress()
        }
    }

    fun getFilteredCallList(callList: List<CallWithFilterUIModel>, searchQuery: String, filterIndexes: ArrayList<Int>) {
        launch {
            val filteredCallList = listCallUseCase.getFilteredCallList(callWithFilterUIMapper.mapFromUIModelList(callList), searchQuery, filterIndexes)
            filteredCallListLiveData.postValue(callWithFilterUIMapper.mapToUIModelList(filteredCallList))
            hideProgress()
        }
    }

    fun getHashMapFromCallList(callList: List<CallWithFilterUIModel>, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        val hashMapList = callList.sortedByDescending {
                it.callDate
            }.groupBy { it.dateFromCallDate() }
        callHashMapLiveData.postValue(hashMapList)
        hideProgress()
    }

    fun deleteCallList(filteredCallIdList: List<Int>) {
        showProgress()
        launch {
            listCallUseCase.deleteCallList(filteredCallIdList, application.isNetworkAvailable()) { operationResult ->
                when(operationResult) {
                    is Result.Success -> successDeleteNumberLiveData.postValue(true)
                    is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                }
                hideProgress()
            }
        }
    }
}
