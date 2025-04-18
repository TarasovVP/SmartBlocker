package com.tarasovvp.smartblocker.presentation.main.number.create

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import com.tarasovvp.smartblocker.domain.entities.dbviews.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.CreateFilterUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.uimodels.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithCountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateFilterViewModel
    @Inject
    constructor(
        private val application: Application,
        private val createFilterUseCase: CreateFilterUseCase,
        private val filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper,
        private val contactWithFilterUIMapper: ContactWithFilterUIMapper,
    ) : BaseViewModel(application) {
        val contactWithFilterLiveData = MutableLiveData<List<ContactWithFilterUIModel>>()
        val existingFilterLiveData = MutableLiveData<FilterWithFilteredNumberUIModel>()
        val filterActionLiveData = MutableLiveData<FilterWithFilteredNumberUIModel>()

        fun getMatchedContactWithFilterList(filterWithCountryCodeUIModel: FilterWithCountryCodeUIModel?) {
            showProgress()
            launch {
                val contactWithFilters =
                    createFilterUseCase.allContactsWithFiltersByCreateFilter(
                        filterWithCountryCodeUIModel?.filterWithFilteredNumberUIModel?.filter.orEmpty(),
                        filterWithCountryCodeUIModel?.countryCodeUIModel?.country.orEmpty(),
                        filterWithCountryCodeUIModel?.countryCodeUIModel?.countryCode.orEmpty(),
                        filterWithCountryCodeUIModel?.filterWithFilteredNumberUIModel?.isTypeContain()
                            .isTrue(),
                    )
                contactWithFilterLiveData.postValue(
                    contactWithFilterUIMapper.mapToUIModelList(
                        contactWithFilters,
                    ),
                )
                hideProgress()
            }
        }

        fun checkFilterExist(filter: String) {
            launch {
                val existingFilter =
                    createFilterUseCase.getFilter(filter) ?: FilterWithFilteredNumber(
                        Filter(filterType = DEFAULT_FILTER),
                    )
                existingFilterLiveData.postValue(
                    filterWithFilteredNumberUIMapper.mapToUIModel(
                        existingFilter,
                    ),
                )
            }
        }

        fun createFilter(filterWithCountryCode: FilterWithFilteredNumberUIModel) {
            showProgress()
            launch {
                filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithCountryCode).filter?.let { filter ->
                    createFilterUseCase.createFilter(
                        filter,
                        application.isNetworkAvailable(),
                    ) { operationResult ->
                        when (operationResult) {
                            is Result.Success ->
                                filterWithCountryCode.let {
                                    filterActionLiveData.postValue(
                                        it,
                                    )
                                }

                            is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                        }
                    }
                }
                hideProgress()
            }
        }

        fun updateFilter(filterWithCountryCode: FilterWithFilteredNumberUIModel) {
            showProgress()
            launch {
                filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithCountryCode).filter?.let { filter ->
                    createFilterUseCase.updateFilter(
                        filter,
                        application.isNetworkAvailable(),
                    ) { operationResult ->
                        when (operationResult) {
                            is Result.Success ->
                                filterWithCountryCode.let {
                                    filterActionLiveData.postValue(
                                        it,
                                    )
                                }

                            is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                        }
                    }
                }
                hideProgress()
            }
        }

        fun deleteFilter(filterWithCountryCode: FilterWithFilteredNumberUIModel) {
            showProgress()
            launch {
                filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithCountryCode).filter?.let { filter ->
                    createFilterUseCase.deleteFilter(
                        filter,
                        application.isNetworkAvailable(),
                    ) { operationResult ->
                        when (operationResult) {
                            is Result.Success ->
                                filterWithCountryCode.let {
                                    filterActionLiveData.postValue(
                                        it,
                                    )
                                }

                            is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                        }
                    }
                }
                hideProgress()
            }
        }
    }
