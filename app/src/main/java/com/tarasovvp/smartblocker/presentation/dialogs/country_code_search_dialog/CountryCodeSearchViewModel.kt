package com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.data.database.entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
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