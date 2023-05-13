package com.tarasovvp.smartblocker.presentation.main.number.create

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
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
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithCountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CreateFilterViewModel @Inject constructor(
    private val application: Application,
    private val createFilterUseCase: CreateFilterUseCase,
    private val countryCodeUIMapper: CountryCodeUIMapper,
    private val filterWithCountryCodeUIMapper: FilterWithCountryCodeUIMapper,
    private val callWithFilterUIMapper: CallWithFilterUIMapper,
    private val contactWithFilterUIMapper: ContactWithFilterUIMapper
) : BaseViewModel(application) {

    val countryCodeLiveData = MutableLiveData<CountryCodeUIModel>()
    val numberDataListLiveDataUIModel = MutableLiveData<List<NumberDataUIModel>>()
    val existingFilterLiveData = MutableLiveData<FilterWithCountryCodeUIModel>()
    val filteredNumberDataListLiveDataUIModel = MutableLiveData<ArrayList<NumberDataUIModel>>()
    val filterActionLiveData = MutableLiveData<FilterWithCountryCodeUIModel>()

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
            val calls =  createFilterUseCase.allCallsByFilter(filter)
            val contacts =  createFilterUseCase.allContactsByFilter(filter)
            val numberDataUIModelList = ArrayList<NumberDataUIModel>().apply {
                addAll(callWithFilterUIMapper.mapToUIModelList(calls))
                addAll(contactWithFilterUIMapper.mapToUIModelList(contacts))
                sortWith(compareBy(
                        {
                            when (it) {
                                is ContactWithFilterUIModel -> it.contactUIModel?.number?.startsWith(Constants.PLUS_CHAR)
                                is CallWithFilterUIModel -> it.callUIModel?.number?.startsWith(Constants.PLUS_CHAR)
                                else -> false
                            }
                        },
                        {
                            when (it) {
                                is ContactWithFilterUIModel -> it.contactUIModel?.number
                                is CallWithFilterUIModel -> it.callUIModel?.number
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
            val existingFilter = createFilterUseCase.getFilter(filter) ?: FilterWithCountryCode(Filter(filterType = DEFAULT_FILTER))
            existingFilterLiveData.postValue(filterWithCountryCodeUIMapper.mapToUIModel(existingFilter))
        }
    }

    fun createFilter(filterWithCountryCode: FilterWithCountryCodeUIModel) {
        Timber.e("CreateFilterViewModel createFilter createFilter $filterWithCountryCode filter.country ${filterWithCountryCode.filterUIModel?.country}")
        launch {
            filterWithCountryCodeUIMapper.mapFromUIModel(filterWithCountryCode).filter?.let { filter ->
                createFilterUseCase.createFilter(filter, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { operationResult ->
                    when(operationResult) {
                        is Result.Success -> filterWithCountryCode.let { filterActionLiveData.postValue(it) }
                        is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                    }
                }
            }
        }
    }

    fun updateFilter(filterWithCountryCode: FilterWithCountryCodeUIModel) {
        Timber.e("CreateFilterViewModel updateFilter")
        launch {
            filterWithCountryCodeUIMapper.mapFromUIModel(filterWithCountryCode).filter?.let { filter ->
                createFilterUseCase.updateFilter(filter, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { operationResult ->
                    when(operationResult) {
                        is Result.Success -> filterWithCountryCode.let { filterActionLiveData.postValue(it) }
                        is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                    }
                }
            }
        }
    }

    fun deleteFilter(filterWithCountryCode: FilterWithCountryCodeUIModel) {
        Timber.e("CreateFilterViewModel deleteFilter")
        launch {
            filterWithCountryCodeUIMapper.mapFromUIModel(filterWithCountryCode).filter?.let { filter ->
                createFilterUseCase.deleteFilter(filter, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { operationResult ->
                    when(operationResult) {
                        is Result.Success -> filterWithCountryCode.let { filterActionLiveData.postValue(it) }
                        is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                    }
                }
            }
        }
    }
}