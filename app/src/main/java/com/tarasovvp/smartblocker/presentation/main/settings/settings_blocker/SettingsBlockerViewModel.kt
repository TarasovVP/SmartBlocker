package com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.data.database.entities.CountryCode
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsBlockerViewModel @Inject constructor(
    application: Application,
    private val realDataBaseRepository: RealDataBaseRepository,
    private val countryCodeRepository: CountryCodeRepository
) : BaseViewModel(application) {

    val successBlockHiddenLiveData = MutableLiveData<Boolean>()
    val countryCodeLiveData = MutableLiveData<CountryCode>()

    fun changeBlockHidden(blockHidden: Boolean) {
        showProgress()
        launch {
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                realDataBaseRepository.changeBlockHidden(blockHidden) {
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
                country?.let { countryCodeRepository.getCountryCodeWithCountry(it) }
                    ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }
}