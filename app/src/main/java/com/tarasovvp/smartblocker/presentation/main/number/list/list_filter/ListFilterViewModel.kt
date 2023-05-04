package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.usecase.ListFilterUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ListFilterViewModel @Inject constructor(
    application: Application,
    private val listFilterUseCase: ListFilterUseCase
) : BaseViewModel(application) {

    val filterListLiveData = MutableLiveData<ArrayList<FilterWithCountryCode>?>()
    val successDeleteFilterLiveData = MutableLiveData<Boolean>()
    val filteredFilterListLiveData = MutableLiveData<List<FilterWithCountryCode>>()
    val filterHashMapLiveData = MutableLiveData<Map<String, List<FilterWithCountryCode>>?>()

    fun getFilterList(isBlackList: Boolean, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val filterArrayList =
                listFilterUseCase.getFilterList(isBlackList).orEmpty()
            filterListLiveData.postValue(ArrayList(filterArrayList))
            Timber.e("ListFilterViewModel getFilterList $filterArrayList")
            hideProgress()
        }
    }

    fun getFilteredFilterList(filterList: List<FilterWithCountryCode>, searchQuery: String, filterIndexes: ArrayList<Int>) {
        launch {
            filteredFilterListLiveData.postValue(listFilterUseCase.getFilteredFilterList(filterList, searchQuery, filterIndexes))
        }
    }

    fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            filterHashMapLiveData.postValue(
                listFilterUseCase.getHashMapFromFilterList(filterList)
            )
            hideProgress()
        }
    }

    fun deleteFilterList(filterList: List<Filter?>) {
        showProgress()
        launch {
            listFilterUseCase.deleteFilterList(filterList, SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                successDeleteFilterLiveData.postValue(true)
            }
            hideProgress()
        }
    }
}