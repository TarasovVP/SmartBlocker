package com.tarasovvp.smartblocker.presentation.main.number.details.details_filter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.DetailsFilterUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsFilterViewModel @Inject constructor(
    private val application: Application,
    private val detailsFilterUseCase: DetailsFilterUseCase
) : BaseViewModel(application) {

    val numberDataListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val filteredNumberDataListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val filteredCallListLiveData = MutableLiveData<ArrayList<NumberData>>()

    val filterActionLiveData = MutableLiveData<Filter>()

    fun getQueryContactCallList(filter: Filter) {
        showProgress()
        launch {
            val numberDataList = detailsFilterUseCase.getQueryContactCallList(filter)
            numberDataListLiveData.postValue(numberDataList)
        }
    }

    fun filteredNumberDataList(
        filter: Filter?,
        numberDataList: ArrayList<NumberData>,
        color: Int,
    ) {
        launch {
            filteredNumberDataListLiveData.postValue(
                detailsFilterUseCase.filteredNumberDataList(
                    filter,
                    numberDataList, color
                )
            )
            hideProgress()
        }
    }

    fun filteredCallsByFilter(filter: String) {
        launch {
            val filteredCallList = detailsFilterUseCase.filteredCallsByFilter(filter)
            filteredCallList.let { filteredCalls ->
                filteredCallListLiveData.postValue(ArrayList(filteredCalls))
            }
        }
    }

    fun deleteFilter(filter: Filter?) {
        showProgress()
        launch {
            filter?.let {
                detailsFilterUseCase.deleteFilter(it, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { operationResult ->
                    when(operationResult) {
                        is Result.Success -> filterActionLiveData.postValue(it)
                        is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                    }
                }
            }
            hideProgress()
        }
    }

    fun updateFilter(filter: Filter?) {
        showProgress()
        launch {
            filter?.let {
                detailsFilterUseCase.updateFilter(it, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { operationResult ->
                    when(operationResult) {
                        is Result.Success -> filterActionLiveData.postValue(it)
                        is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                    }
                }
            }
            hideProgress()
        }
    }
}