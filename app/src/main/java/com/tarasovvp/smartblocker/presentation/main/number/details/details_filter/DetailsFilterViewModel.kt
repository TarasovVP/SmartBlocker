package com.tarasovvp.smartblocker.presentation.main.number.details.details_filter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.DetailsFilterUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsFilterViewModel
    @Inject
    constructor(
        private val application: Application,
        private val detailsFilterUseCase: DetailsFilterUseCase,
        private val filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper,
        private val callWithFilterUIMapper: CallWithFilterUIMapper,
        private val contactWithFilterUIMapper: ContactWithFilterUIMapper,
    ) : BaseViewModel(application) {
        val numberDataListLiveDataUIModel = MutableLiveData<ArrayList<NumberDataUIModel>>()
        val filteredCallListLiveData = MutableLiveData<ArrayList<NumberDataUIModel>>()
        val filterActionLiveData = MutableLiveData<FilterWithFilteredNumberUIModel>()

        fun getQueryContactCallList(filter: String) {
            showProgress()
            launch {
                val contacts = detailsFilterUseCase.allContactsWithFiltersByFilter(filter)
                val numberDataUIModelList =
                    ArrayList<NumberDataUIModel>().apply {
                        addAll(contactWithFilterUIMapper.mapToUIModelList(contacts))
                    }
                numberDataListLiveDataUIModel.postValue(numberDataUIModelList)
                hideProgress()
            }
        }

        fun filteredCallsByFilter(filter: String) {
            showProgress()
            launch {
                val filteredCallList = detailsFilterUseCase.allFilteredCallsByFilter(filter)
                filteredCallListLiveData.postValue(
                    ArrayList(
                        callWithFilterUIMapper.mapToUIModelList(
                            filteredCallList,
                        ),
                    ),
                )
                hideProgress()
            }
        }

        fun deleteFilter(filterWithCountryCode: FilterWithFilteredNumberUIModel) {
            showProgress()
            launch {
                filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithCountryCode).filter?.let { filter ->
                    detailsFilterUseCase.deleteFilter(
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
                    detailsFilterUseCase.updateFilter(
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
