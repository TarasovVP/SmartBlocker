package com.tarasovvp.smartblocker.presentation.main.number.create

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.CreateFilterUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CreateFilterViewModel @Inject constructor(
    private val application: Application,
    private val createFilterUseCase: CreateFilterUseCase,
    private val countryCodeUIMapper: CountryCodeUIMapper,
    private val filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper,
    private val callWithFilterUIMapper: CallWithFilterUIMapper,
    private val contactWithFilterUIMapper: ContactWithFilterUIMapper
) : BaseViewModel(application) {

    val countryCodeLiveData = MutableLiveData<CountryCodeUIModel>()
    val numberDataListLiveDataUIModel = MutableLiveData<List<NumberDataUIModel>>()
    val existingFilterLiveData = MutableLiveData<FilterWithFilteredNumberUIModel>()
    val filteredNumberDataListLiveDataUIModel = MutableLiveData<ArrayList<NumberDataUIModel>>()
    val filterActionLiveData = MutableLiveData<FilterWithFilteredNumberUIModel>()

    fun getCountryCodeWithCode(code: Int?) {
        Timber.e("CreateFilterViewModel getCountryCodeWithCode code $code")
        launch {
            val countryCode =
                code?.let { createFilterUseCase.getCountryCodeWithCode(it) } ?: CountryCode()
            countryCodeLiveData.postValue(countryCodeUIMapper.mapToUIModel(countryCode))
        }
    }

    fun getNumberDataList(filter: String) {
        Timber.e("CreateFilterViewModel getNumberDataList showProgress")
        launch {
            val calls =  createFilterUseCase.allCallWithFiltersByFilter(filter)
            val contacts =  createFilterUseCase.allContactsWithFiltersByFilter(filter)
            val numberDataUIModelList = ArrayList<NumberDataUIModel>().apply {
                addAll(callWithFilterUIMapper.mapToUIModelList(calls))
                addAll(contactWithFilterUIMapper.mapToUIModelList(contacts))
                sortWith(compareBy(
                        {
                            when (it) {
                                is ContactWithFilterUIModel -> it.number.startsWith(Constants.PLUS_CHAR)
                                is CallWithFilterUIModel -> it.number.startsWith(Constants.PLUS_CHAR)
                                else -> false
                            }
                        },
                        {
                            when (it) {
                                is ContactWithFilterUIModel -> it.number
                                is CallWithFilterUIModel -> it.number
                                else -> String.EMPTY
                            }
                        }
                    ))
            }
            numberDataListLiveDataUIModel.postValue(numberDataUIModelList)
        }
    }

    fun checkFilterExist(filter: String) {
        Timber.e("CreateFilterViewModel checkFilterExist filter $filter")
        launch {
            val existingFilter = createFilterUseCase.getFilter(filter) ?: FilterWithFilteredNumbers(Filter(filterType = DEFAULT_FILTER))
            existingFilterLiveData.postValue(filterWithFilteredNumberUIMapper.mapToUIModel(existingFilter))
        }
    }

    fun createFilter(filterWithCountryCode: FilterWithFilteredNumberUIModel) {
        Timber.e("CreateFilterViewModel createFilter createFilter $filterWithCountryCode filter.country ${filterWithCountryCode.country}")
        launch {
            filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithCountryCode).filter?.let { filter ->
                createFilterUseCase.createFilter(filter, application.isNetworkAvailable()) { operationResult ->
                    when(operationResult) {
                        is Result.Success -> filterWithCountryCode.let { filterActionLiveData.postValue(it) }
                        is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                    }
                }
            }
        }
    }

    fun updateFilter(filterWithCountryCode: FilterWithFilteredNumberUIModel) {
        Timber.e("CreateFilterViewModel updateFilter")
        launch {
            filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithCountryCode).filter?.let { filter ->
                createFilterUseCase.updateFilter(filter, application.isNetworkAvailable()) { operationResult ->
                    when(operationResult) {
                        is Result.Success -> filterWithCountryCode.let { filterActionLiveData.postValue(it) }
                        is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                    }
                }
            }
        }
    }

    fun deleteFilter(filterWithCountryCode: FilterWithFilteredNumberUIModel) {
        Timber.e("CreateFilterViewModel deleteFilter")
        launch {
            filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithCountryCode).filter?.let { filter ->
                createFilterUseCase.deleteFilter(filter, application.isNetworkAvailable()) { operationResult ->
                    when(operationResult) {
                        is Result.Success -> filterWithCountryCode.let { filterActionLiveData.postValue(it) }
                        is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                    }
                }
            }
        }
    }
}