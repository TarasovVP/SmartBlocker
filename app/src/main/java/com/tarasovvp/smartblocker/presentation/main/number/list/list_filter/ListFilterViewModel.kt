package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.data.database.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.data.database.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
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
            Timber.e("ListFilterViewModel getFilterList $filterArrayList")
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