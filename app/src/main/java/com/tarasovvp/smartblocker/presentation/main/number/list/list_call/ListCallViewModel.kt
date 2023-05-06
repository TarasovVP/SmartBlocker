package com.tarasovvp.smartblocker.presentation.main.number.list.list_call

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.ListCallUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListCallViewModel @Inject constructor(
    private val application: Application,
    private val listCallUseCase: ListCallUseCase
) : BaseViewModel(application) {

    val callListLiveData = MutableLiveData<List<CallWithFilter>>()
    val filteredCallListLiveData = MutableLiveData<List<CallWithFilter>>()
    val callHashMapLiveData = MutableLiveData<Map<String, List<CallWithFilter>>?>()
    val successDeleteNumberLiveData = MutableLiveData<Boolean>()

    fun getCallList(refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val callList = listCallUseCase.getCallList()
            callListLiveData.postValue(callList)
            hideProgress()
        }
    }

    fun getFilteredCallList(callList: List<CallWithFilter>, searchQuery: String, filterIndexes: ArrayList<Int>) {
        launch {
            filteredCallListLiveData.postValue(listCallUseCase.getFilteredCallList(callList, searchQuery, filterIndexes))
            hideProgress()
        }
    }

    fun getHashMapFromCallList(callList: List<CallWithFilter>, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val hashMapList =
                listCallUseCase.getHashMapFromCallList(callList.sortedByDescending {
                    it.call?.callDate
                })
            callHashMapLiveData.postValue(hashMapList)
            hideProgress()
        }
    }

    fun deleteCallList(filteredCallIdList: List<Int>) {
        showProgress()
        launch {
            listCallUseCase.deleteCallList(filteredCallIdList, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { operationResult ->
                when(operationResult) {
                    is Result.Success -> successDeleteNumberLiveData.postValue(true)
                    is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                }
                hideProgress()
            }
        }
    }
}
