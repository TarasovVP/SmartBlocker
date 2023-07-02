package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.ListFilterUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.isContaining
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import com.tarasovvp.smartblocker.utils.extensions.isNotNull

open class BaseListFilterViewModel(
    private val application: Application,
    private val listFilterUseCase: ListFilterUseCase,
    private val filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper,
    private val countryCodeUIMapper: CountryCodeUIMapper
) : BaseViewModel(application) {

    val filterListLiveData = MutableLiveData<List<FilterWithFilteredNumberUIModel>?>()
    val successDeleteFilterLiveData = MutableLiveData<Boolean>()
    val filteredFilterListLiveData = MutableLiveData<List<FilterWithFilteredNumberUIModel>>()
    val currentCountryCodeLiveData = MutableLiveData<CountryCodeUIModel>()

    fun getFilterList(isBlackList: Boolean, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val allFiltersByType = listFilterUseCase.allFilterWithFilteredNumbersByType(isBlackList).orEmpty()
            filterListLiveData.postValue(filterWithFilteredNumberUIMapper.mapToUIModelList(allFiltersByType))
            hideProgress()
        }
    }

    fun getFilteredFilterList(filterList: List<FilterWithFilteredNumberUIModel>, searchQuery: String, filterIndexes: ArrayList<Int>) {
        launch {
            val filteredFilterList = if (searchQuery.isBlank() && filterIndexes.isEmpty()) filterList else filterList.filter { filterWithCountryCode ->
                (filterWithCountryCode.filter isContaining  searchQuery)
                        && (filterIndexes.contains(NumberDataFiltering.FILTER_CONDITION_FULL_FILTERING.ordinal) && filterWithCountryCode.conditionType == FilterCondition.FILTER_CONDITION_FULL.ordinal
                        || filterIndexes.contains(NumberDataFiltering.FILTER_CONDITION_START_FILTERING.ordinal) && filterWithCountryCode.conditionType == FilterCondition.FILTER_CONDITION_START.ordinal
                        || filterIndexes.contains(NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING.ordinal) && filterWithCountryCode.conditionType == FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
                        || filterIndexes.isEmpty())
            }
            filteredFilterListLiveData.postValue(filteredFilterList)
        }
    }

    fun deleteFilterList(filterList: List<FilterWithFilteredNumberUIModel>) {
        showProgress()
        launch {
            val filterListToDelete = filterWithFilteredNumberUIMapper.mapFromUIModelList(filterList).mapNotNull { it.filter }
            listFilterUseCase.deleteFilterList(filterListToDelete, application.isNetworkAvailable()) { operationResult ->
                when(operationResult) {
                    is Result.Success -> successDeleteFilterLiveData.postValue(true)
                    is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                }
            }
            hideProgress()
        }
    }

    fun getCurrentCountryCode() {
        launch {
            listFilterUseCase.getCurrentCountryCode().collect { countryCode ->
                currentCountryCodeLiveData.postValue(countryCode.takeIf { it.isNotNull() }?.let { countryCodeUIMapper.mapToUIModel(it) } ?: CountryCodeUIModel())
            }
        }
    }
}