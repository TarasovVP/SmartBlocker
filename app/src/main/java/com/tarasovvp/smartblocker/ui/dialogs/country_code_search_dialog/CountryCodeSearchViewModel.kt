package com.tarasovvp.smartblocker.ui.dialogs.country_code_search_dialog

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.models.CountryCode
import com.tarasovvp.smartblocker.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel

class CountryCodeSearchViewModel(application: Application) : BaseViewModel(application) {

    private val countryCodeRepository = CountryCodeRepository

    val countryCodeListLiveData = MutableLiveData<List<CountryCode>>()

    fun getCountryCodeList() {
        launch {
            val countryCodeList = countryCodeRepository.getAllCountryCodes()
            countryCodeList?.apply {
                countryCodeListLiveData.postValue(this)
            }
        }
    }
}