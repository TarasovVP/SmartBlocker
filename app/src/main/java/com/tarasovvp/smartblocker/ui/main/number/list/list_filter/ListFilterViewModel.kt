package com.tarasovvp.smartblocker.ui.main.number.list.list_filter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.repository.FilterRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel

class ListFilterViewModel(application: Application) : BaseViewModel(application) {

    val filterListLiveData = MutableLiveData<ArrayList<Filter>?>()
    val successDeleteFilterLiveData = MutableLiveData<Boolean>()

    private val filterRepository = FilterRepository

    val filterHashMapLiveData = MutableLiveData<Map<String, List<Filter>>?>()

    fun getFilterList(isBlackList: Boolean, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val filterArrayList =
                filterRepository.allFiltersByType(if (isBlackList) BLOCKER else PERMISSION) as ArrayList
            filterListLiveData.postValue(filterArrayList)
            hideProgress()
        }
    }

    fun getHashMapFromFilterList(filterList: List<Filter>, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            filterHashMapLiveData.postValue(
                filterRepository.getHashMapFromFilterList(filterList)
            )
            hideProgress()
        }
    }

    fun deleteFilterList(filterList: List<Filter>) {
        showProgress()
        launch {
            filterRepository.deleteFilterList(filterList) {
                successDeleteFilterLiveData.postValue(true)
            }
            hideProgress()
        }
    }
}