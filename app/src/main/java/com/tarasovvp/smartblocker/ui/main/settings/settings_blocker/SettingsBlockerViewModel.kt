package com.tarasovvp.smartblocker.ui.main.settings.settings_blocker

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.database.entities.CountryCode
import com.tarasovvp.smartblocker.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
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
        Log.e("createFilterTAG", "CreateFilterViewModel getCountryCodeWithCountry country $country")
        launch {
            val countryCode =
                country?.let { countryCodeRepository.getCountryCodeWithCountry(it) }
                    ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }
}