package com.tarasovvp.smartblocker.ui.main.number.list.list_filter

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.database.entities.Filter
import com.tarasovvp.smartblocker.database.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.repository.FilterRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListFilterViewModel @Inject constructor(
    application: Application,
    private val filterRepository: FilterRepository
) : BaseViewModel(application) {

    val filterListLiveData = MutableLiveData<ArrayList<FilterWithCountryCode>?>()
    val successDeleteFilterLiveData = MutableLiveData<Boolean>()
    val filterHashMapLiveData = MutableLiveData<Map<String, List<FilterWithCountryCode>>?>()

    fun getFilterList(isBlackList: Boolean, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val filterArrayList =
                filterRepository.allFiltersByType(if (isBlackList) BLOCKER else PERMISSION) as ArrayList
            filterListLiveData.postValue(filterArrayList)
            Log.e("filterTAG", "ListFilterViewModel getFilterList $filterArrayList")
            hideProgress()
        }
    }

    fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            filterHashMapLiveData.postValue(
                filterRepository.getHashMapFromFilterList(filterList)
            )
            hideProgress()
        }
    }

    fun deleteFilterList(filterList: List<Filter?>) {
        showProgress()
        launch {
            filterRepository.deleteFilterList(filterList) {
                successDeleteFilterLiveData.postValue(true)
            }
            hideProgress()
        }
    }
}