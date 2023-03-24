package com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.usecase.countrycode_search.CountryCodeSearchUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CountryCodeSearchViewModel @Inject constructor(
    application: Application,
    private val countryCodeSearchUseCase: CountryCodeSearchUseCase
) : BaseViewModel(application) {

    val countryCodeListLiveData = MutableLiveData<List<CountryCode>>()

    fun getCountryCodeList() {
        launch {
            val countryCodeList = countryCodeSearchUseCase.getCountryCodeList()
            countryCodeList.apply {
                countryCodeListLiveData.postValue(this)
            }
        }
    }
}