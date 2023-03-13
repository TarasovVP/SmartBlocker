package com.tarasovvp.smartblocker.ui.dialogs.country_code_search_dialog

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.models.CountryCode
import com.tarasovvp.smartblocker.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CountryCodeSearchViewModel @Inject constructor(
    application: Application,
    private val countryCodeRepository: CountryCodeRepository
) : BaseViewModel(application) {

    val countryCodeListLiveData = MutableLiveData<List<CountryCode>>()

    fun getCountryCodeList() {
        launch {
            val countryCodeList = countryCodeRepository.getAllCountryCodes()
            countryCodeList.apply {
                countryCodeListLiveData.postValue(this)
            }
        }
    }
}