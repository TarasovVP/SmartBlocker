package com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.usecase.DetailsNumberDataUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsNumberDataViewModel @Inject constructor(
    application: Application,
    private val detailsNumberDataUseCase: DetailsNumberDataUseCase
) : BaseViewModel(application) {

    val filterListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val filteredCallListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val countryCodeLiveData = MutableLiveData<CountryCode>()

    fun filterListWithNumber(number: String) {
        showProgress()
        launch {
            val filterList = detailsNumberDataUseCase.filterListWithNumber(number)
            filterList.let {
                filterListLiveData.postValue(ArrayList(it))
            }
        }
    }

    fun filteredCallsByNumber(number: String) {
        launch {
            val filteredCallList = detailsNumberDataUseCase.filteredCallsByNumber(number)
            filteredCallList.let { filteredCalls ->
                filteredCallListLiveData.postValue(ArrayList(filteredCalls))
            }
            hideProgress()
        }
    }

    fun getCountryCode(code: Int?) {
        launch {
            val countryCode =
                code?.let { detailsNumberDataUseCase.getCountryCode(it) } ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }
}