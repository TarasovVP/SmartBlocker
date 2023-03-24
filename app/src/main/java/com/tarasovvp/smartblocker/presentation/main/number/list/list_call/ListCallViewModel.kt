package com.tarasovvp.smartblocker.presentation.main.number.list.list_call

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_call.ListCallUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListCallViewModel @Inject constructor(
    application: Application,
    private val listCallUseCase: ListCallUseCase
) : BaseViewModel(application) {

    val callListLiveData = MutableLiveData<List<CallWithFilter>>()
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
            listCallUseCase.deleteCallList(filteredCallIdList) {
                successDeleteNumberLiveData.postValue(true)
                hideProgress()
            }
        }
    }
}
