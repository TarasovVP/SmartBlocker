package com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.usecases.CountryCodeSearchUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CountryCodeSearchViewModel @Inject constructor(
    application: Application,
    private val countryCodeSearchUseCase: CountryCodeSearchUseCase
) : BaseViewModel(application) {

    val countryCodeListLiveData = MutableLiveData<List<CountryCodeUIModel>>()

    fun getCountryCodeList() {
        launch {
            val countryCodeList = countryCodeSearchUseCase.getCountryCodeList()
            countryCodeList.apply {
                countryCodeListLiveData.postValue(this)
            }
        }
    }
}