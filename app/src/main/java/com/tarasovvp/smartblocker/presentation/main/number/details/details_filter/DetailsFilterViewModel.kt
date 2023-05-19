package com.tarasovvp.smartblocker.presentation.main.number.details.details_filter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.DetailsFilterUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsFilterViewModel @Inject constructor(
    private val application: Application,
    private val detailsFilterUseCase: DetailsFilterUseCase,
    private val filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper,
    private val callWithFilterUIMapper: CallWithFilterUIMapper,
    private val contactWithFilterUIMapper: ContactWithFilterUIMapper
) : BaseViewModel(application) {

    val numberDataListLiveDataUIModel = MutableLiveData<ArrayList<NumberDataUIModel>>()
    val filteredCallListLiveData = MutableLiveData<ArrayList<NumberDataUIModel>>()
    val filterActionLiveData = MutableLiveData<FilterWithFilteredNumberUIModel>()

    fun getQueryContactCallList(filter: String) {
        showProgress()
        launch {
            val calls =  detailsFilterUseCase.allCallWithFiltersByFilter(filter)
            val contacts =  detailsFilterUseCase.allContactsWithFiltersByFilter(filter)
            val numberDataUIModelList = ArrayList<NumberDataUIModel>().apply {
                addAll(callWithFilterUIMapper.mapToUIModelList(calls))
                addAll(contactWithFilterUIMapper.mapToUIModelList(contacts))
                sortWith(compareBy(
                    {
                        when (it) {
                            is ContactWithFilterUIModel -> it.number.startsWith(PLUS_CHAR)
                            is CallWithFilterUIModel -> it.number.startsWith(PLUS_CHAR)
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

    fun filteredCallsByFilter(filter: String) {
        launch {
            val filteredCallList = detailsFilterUseCase.allFilteredCallsByFilter(filter)
            filteredCallListLiveData.postValue(ArrayList(callWithFilterUIMapper.mapToUIModelList(filteredCallList)))
        }
    }

    fun deleteFilter(filterWithCountryCode: FilterWithFilteredNumberUIModel) {
        showProgress()
        launch {
            filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithCountryCode).filter?.let { filter ->
                detailsFilterUseCase.deleteFilter(filter, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { operationResult ->
                    when(operationResult) {
                        is Result.Success -> filterWithCountryCode.let { filterActionLiveData.postValue(it) }
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
                detailsFilterUseCase.updateFilter(filter, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { operationResult ->
                    when(operationResult) {
                        is Result.Success -> filterWithCountryCode.let { filterActionLiveData.postValue(it) }
                        is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                    }
                }
            }
            hideProgress()
        }
    }
}