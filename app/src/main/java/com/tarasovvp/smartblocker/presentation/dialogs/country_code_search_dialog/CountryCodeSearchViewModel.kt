package com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.usecases.CountryCodeSearchUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CountryCodeSearchViewModel @Inject constructor(
    application: Application,
    private val countryCodeSearchUseCase: CountryCodeSearchUseCase,
    private val countryCodeUIMapper: CountryCodeUIMapper
) : BaseViewModel(application) {

    val appLangLiveDataLiveData = MutableLiveData<String>()
    val countryCodeListLiveData = MutableLiveData<List<CountryCodeUIModel>>()

    fun getAppLanguage() {
        launch {
            countryCodeSearchUseCase.getAppLanguage().collect { appLang ->
                appLangLiveDataLiveData.postValue(appLang ?: Locale.getDefault().language)
            }
        }
    }

    fun getCountryCodeList() {
        launch {
            val countryCodeList = countryCodeSearchUseCase.getCountryCodeList()
            countryCodeListLiveData.postValue(countryCodeUIMapper.mapToUIModelList(countryCodeList))
        }
    }
}