package com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.presentation.ui_models.NumberData
import com.tarasovvp.smartblocker.domain.usecases.DetailsNumberDataUseCase
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
            val filterList = detailsNumberDataUseCase.allFilterWithCountryCodesByNumber(number)
            filterList.let {
                filterListLiveData.postValue(ArrayList(it))
            }
        }
    }

    fun filteredCallsByNumber(number: String) {
        launch {
            val filteredCallList = detailsNumberDataUseCase.allFilteredCallsByNumber(number)
            filteredCallListLiveData.postValue(ArrayList(filteredCallList))
            hideProgress()
        }
    }

    fun getCountryCode(code: Int?) {
        launch {
            val countryCode =
                code?.let { detailsNumberDataUseCase.getCountryCodeWithCode(it) } ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }
}