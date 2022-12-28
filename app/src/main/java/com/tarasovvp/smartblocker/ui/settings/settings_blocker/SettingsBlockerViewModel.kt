package com.tarasovvp.smartblocker.ui.settings.settings_blocker

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.models.CountryCode
import com.tarasovvp.smartblocker.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel

class SettingsBlockerViewModel(application: Application) : BaseViewModel(application) {

    private val realDataBaseRepository = RealDataBaseRepository
    private val countryCodeRepository = CountryCodeRepository

    val successBlockHiddenLiveData = MutableLiveData<Boolean>()
    val countryCodeLiveData = MutableLiveData<CountryCode>()

    fun changeBlockHidden(blockHidden: Boolean) {
        showProgress()
        launch {
            if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
                realDataBaseRepository.changeBlockHidden(blockHidden) {
                    successBlockHiddenLiveData.postValue(blockHidden)
                }
            } else {
                SharedPreferencesUtil.blockHidden = blockHidden
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