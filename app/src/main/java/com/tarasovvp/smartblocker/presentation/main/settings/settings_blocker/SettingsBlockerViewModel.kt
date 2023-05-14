package com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsBlockerViewModel @Inject constructor(
    private val application: Application,
    private val settingsBlockerUseCase: SettingsBlockerUseCase
) : BaseViewModel(application) {

    val blockerTurnOffLiveData = MutableLiveData<Boolean>()
    val blockHiddenLiveData = MutableLiveData<Boolean>()
    val currentCountryCodeLiveData = MutableLiveData<CountryCode>()
    val successBlockHiddenLiveData = MutableLiveData<Boolean>()

    fun getBlockerTurnOff() {
        launch {
            settingsBlockerUseCase.getBlockerTurnOff().collect { blockerTurnOff ->
                blockerTurnOffLiveData.postValue(blockerTurnOff.isTrue())
            }
        }
    }

    fun setBlockerTurnOff(blockerTurnOff: Boolean) {
        launch {
            settingsBlockerUseCase.setBlockerTurnOff(blockerTurnOff)
            blockerTurnOffLiveData.postValue(blockerTurnOff)
        }
    }

    fun getBlockHidden() {
        launch {
            settingsBlockerUseCase.getBlockHidden().collect { blockHidden ->
                blockHiddenLiveData.postValue(blockHidden.isTrue())
            }
        }
    }

    fun setBlockHidden(blockHidden: Boolean) {
        launch {
            settingsBlockerUseCase.setBlockHidden(blockHidden)
            blockHiddenLiveData.postValue(blockHidden)
        }
    }

    fun changeBlockHidden(blockHidden: Boolean) {
        showProgress()
        settingsBlockerUseCase.changeBlockHidden(blockHidden, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { result ->
            when (result) {
                is Result.Success -> successBlockHiddenLiveData.postValue(blockHidden)
                is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
            }
        }
        hideProgress()
    }

    fun getCurrentCountryCode() {
        launch {
            settingsBlockerUseCase.getCurrentCountryCode().collect { countryCode ->
                currentCountryCodeLiveData.postValue(countryCode ?: CountryCode())
            }
        }
    }

    fun setCurrentCountryCode(countryCode: CountryCode) {
        launch {
            settingsBlockerUseCase.setCurrentCountryCode(countryCode)
            currentCountryCodeLiveData.postValue(countryCode)
        }
    }
}