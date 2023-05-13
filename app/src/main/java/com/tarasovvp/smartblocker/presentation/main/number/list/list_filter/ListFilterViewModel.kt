package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.ListFilterUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithCountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithCountryCodeUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ListFilterViewModel @Inject constructor(
    private val application: Application,
    private val listFilterUseCase: ListFilterUseCase,
    private val filterWithCountryCodeUIMapper: FilterWithCountryCodeUIMapper
) : BaseViewModel(application) {

    val filterListLiveData = MutableLiveData<List<FilterWithCountryCodeUIModel>?>()
    val successDeleteFilterLiveData = MutableLiveData<Boolean>()
    val filteredFilterListLiveData = MutableLiveData<List<FilterWithCountryCodeUIModel>>()
    val filterHashMapLiveData = MutableLiveData<Map<String, List<FilterWithCountryCodeUIModel>>?>()

    fun getFilterList(isBlackList: Boolean, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val allFiltersByType = listFilterUseCase.allFilterWithCountryCodesByType(isBlackList).orEmpty()
            filterListLiveData.postValue(filterWithCountryCodeUIMapper.mapToUIModelList(allFiltersByType))
            Timber.e("ListFilterViewModel getFilterList $allFiltersByType")
            hideProgress()
        }
    }

    fun getFilteredFilterList(filterList: List<FilterWithCountryCodeUIModel>, searchQuery: String, filterIndexes: ArrayList<Int>) {
        launch {
            val filteredFilterList = listFilterUseCase.getFilteredFilterList(filterWithCountryCodeUIMapper.mapFromUIModelList(filterList), searchQuery, filterIndexes)
            filteredFilterListLiveData.postValue(filterWithCountryCodeUIMapper.mapToUIModelList(filteredFilterList))
        }
    }

    fun getHashMapFromFilterList(filterList: List<FilterWithCountryCodeUIModel>, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            filterHashMapLiveData.postValue(mapOf(String.EMPTY to filterList))
            hideProgress()
        }
    }

    fun deleteFilterList(filterList: List<FilterWithCountryCodeUIModel>) {
        showProgress()
        launch {
            val filterListToDelete = filterWithCountryCodeUIMapper.mapFromUIModelList(filterList).mapNotNull { it.filter }
            listFilterUseCase.deleteFilterList(filterListToDelete, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { operationResult ->
                when(operationResult) {
                    is Result.Success -> successDeleteFilterLiveData.postValue(true)
                    is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                }
            }
            hideProgress()
        }
    }
}