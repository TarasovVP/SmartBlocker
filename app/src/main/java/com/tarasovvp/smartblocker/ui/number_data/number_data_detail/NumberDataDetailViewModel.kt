package com.tarasovvp.smartblocker.ui.number_data.number_data_detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.model.CountryCode
import com.tarasovvp.smartblocker.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.repository.FilterRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import com.tarasovvp.smartblocker.model.NumberData

class NumberDataDetailViewModel(application: Application) : BaseViewModel(application) {

    private val filterRepository = FilterRepository
    private val countryCodeRepository = CountryCodeRepository

    val filterListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val countryCodeLiveData = MutableLiveData<CountryCode>()

    fun filterListWithNumber(number: String) {
        showProgress()
        launch {
            val filterList = filterRepository.queryFilterList(number)
            filterList?.let {
                filterListLiveData.postValue(ArrayList(it))
            }
            hideProgress()
        }
    }

    fun getCountryCode(code: Int?) {
        launch {
            val countryCode =
                code?.let { countryCodeRepository.getCountryCode(it) } ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }
}