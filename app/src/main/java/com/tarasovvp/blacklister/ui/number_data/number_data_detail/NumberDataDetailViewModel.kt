package com.tarasovvp.blacklister.ui.number_data.number_data_detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.CountryCode
import com.tarasovvp.blacklister.repository.CountryCodeRepository
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import com.tarasovvp.blacklister.ui.number_data.NumberData

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
            val countryCode = code?.let { countryCodeRepository.getCountryCode(it) } ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }
}