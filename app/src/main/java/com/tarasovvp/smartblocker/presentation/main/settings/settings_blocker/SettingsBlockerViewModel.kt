package com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsBlockerViewModel @Inject constructor(
    application: Application,
    private val settingsBlockerUseCase: SettingsBlockerUseCase
) : BaseViewModel(application) {

    val successBlockHiddenLiveData = MutableLiveData<Boolean>()
    val countryCodeLiveData = MutableLiveData<CountryCode>()

    fun changeBlockHidden(blockHidden: Boolean) {
        showProgress()
        launch {
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                settingsBlockerUseCase.changeBlockHidden(blockHidden) {
                    successBlockHiddenLiveData.postValue(blockHidden)
                }
            } else {
                successBlockHiddenLiveData.postValue(blockHidden)
            }
            hideProgress()
        }
    }

    fun getCountryCodeWithCountry(country: String?) {
        launch {
            val countryCode =
                country?.let { settingsBlockerUseCase.getCountryCodeWithCountry(it) }
                    ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }
}