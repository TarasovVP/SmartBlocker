package com.tarasovvp.smartblocker.presentation.main.number.details.details_filter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_filter.DetailsFilterUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsFilterViewModel @Inject constructor(
    application: Application,
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
                detailsFilterUseCase.deleteFilter(it, SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                    filterActionLiveData.postValue(it)
                }
            }
            hideProgress()
        }
    }

    fun updateFilter(filter: Filter?) {
        showProgress()
        launch {
            filter?.let {
                detailsFilterUseCase.updateFilter(it, SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                    filterActionLiveData.postValue(it)
                }
            }
            hideProgress()
        }
    }
}