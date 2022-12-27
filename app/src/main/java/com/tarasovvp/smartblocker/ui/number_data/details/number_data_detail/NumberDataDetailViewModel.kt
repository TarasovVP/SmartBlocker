package com.tarasovvp.smartblocker.ui.number_data.details.number_data_detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.models.CountryCode
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.repository.FilterRepository
import com.tarasovvp.smartblocker.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel

class NumberDataDetailViewModel(application: Application) : BaseViewModel(application) {

    private val filterRepository = FilterRepository
    private val filteredCallRepository = FilteredCallRepository
    private val countryCodeRepository = CountryCodeRepository

    val filterListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val filteredCallListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val countryCodeLiveData = MutableLiveData<CountryCode>()

    fun filterListWithNumber(number: String) {
        showProgress()
        launch {
            val filterList = filterRepository.queryFilterList(number)
            filterList?.let {
                filterListLiveData.postValue(ArrayList(it))
            }
        }
    }

    fun filteredCallsByNumber(number: String) {
        launch {
            val filteredCallList = filteredCallRepository.filteredCallsByNumber(number)
            filteredCallList?.let { filteredCalls ->
                filteredCallListLiveData.postValue(ArrayList(filteredCalls.sortedByDescending { it.callDate }))
            }
            hideProgress()
        }
    }

    fun getCountryCode(code: Int?) {
        launch {
            val countryCode =
                code?.let { countryCodeRepository.getCountryCodeWithCode(it) } ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }
}