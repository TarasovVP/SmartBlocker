package com.tarasovvp.smartblocker.presentation.main.number.create

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.utils.extensions.isDarkMode
import com.tarasovvp.smartblocker.domain.usecases.CreateFilterUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CreateFilterViewModel @Inject constructor(
    private val application: Application,
    private val createFilterUseCase: CreateFilterUseCase
) : BaseViewModel(application) {

    val countryCodeLiveData = MutableLiveData<CountryCode>()
    val numberDataListLiveData = MutableLiveData<List<NumberData>>()
    val existingFilterLiveData = MutableLiveData<FilterWithCountryCode>()
    val filteredNumberDataListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val filterActionLiveData = MutableLiveData<Filter>()

    fun getCountryCodeWithCode(code: Int?) {
        Timber.e("CreateFilterViewModel getCountryCodeWithCode code $code")
        launch {
            val countryCode =
                code?.let { createFilterUseCase.getCountryCodeWithCode(it) } ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }

    fun getNumberDataList() {
        Timber.e("CreateFilterViewModel getNumberDataList showProgress")
        launch {
            val numberDataList = createFilterUseCase.getNumberDataList()
            numberDataListLiveData.postValue(numberDataList)
        }
    }

    fun checkFilterExist(filter: String) {
        Timber.e("CreateFilterViewModel checkFilterExist filter $filter")
        launch {
            val result = createFilterUseCase.getFilter(filter)
            existingFilterLiveData.postValue(result ?: FilterWithCountryCode(Filter(filterType = DEFAULT_FILTER)))
        }
    }

    fun filterNumberDataList(filterWithCountryCode: FilterWithCountryCode?, numberDataList: ArrayList<NumberData>, color: Int) {
        Timber.e("CreateFilterViewModel filterNumberDataList showProgress filter?.filter ${filterWithCountryCode?.filter} numberDataList.size ${numberDataList.size}")
        showProgress()
        launch {
            val filteredNumberDataList = createFilterUseCase.filterNumberDataList(filterWithCountryCode, numberDataList, color)
            filteredNumberDataListLiveData.postValue(filteredNumberDataList)
            hideProgress()
            Timber.e("CreateFilterViewModel filterNumberDataList hideProgress filteredNumberDataList.size ${filteredNumberDataList.size} isDarkMode ${getApplication<Application>().isDarkMode()}")
        }
    }

    fun createFilter(filter: Filter?) {
        Timber.e("CreateFilterViewModel createFilter createFilter $filter filter.country ${filter?.country}")
        launch {
            filter?.let {
                createFilterUseCase.createFilter(it, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { operationResult ->
                    when(operationResult) {
                        is Result.Success -> filterActionLiveData.postValue(it)
                        is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                    }
                }
            }
        }
    }

    fun updateFilter(filter: Filter?) {
        Timber.e("CreateFilterViewModel updateFilter")
        launch {
            filter?.let {
                createFilterUseCase.updateFilter(it, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { operationResult ->
                    when(operationResult) {
                        is Result.Success -> filterActionLiveData.postValue(it)
                        is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                    }
                }
            }
        }
    }

    fun deleteFilter(filter: Filter?) {
        Timber.e("CreateFilterViewModel deleteFilter")
        launch {
            filter?.let {
                createFilterUseCase.deleteFilter(it, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { operationResult ->
                    when(operationResult) {
                        is Result.Success -> filterActionLiveData.postValue(it)
                        is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                    }
                }
            }
        }
    }
}