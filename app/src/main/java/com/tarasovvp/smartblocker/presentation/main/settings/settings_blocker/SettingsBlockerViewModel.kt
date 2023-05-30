package com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsBlockerViewModel @Inject constructor(
    private val application: Application,
    private val settingsBlockerUseCase: SettingsBlockerUseCase
) : BaseViewModel(application) {

    val blockerTurnOnLiveData = MutableLiveData<Boolean>()
    val blockHiddenLiveData = MutableLiveData<Boolean>()
    val currentCountryCodeLiveData = MutableLiveData<CountryCode>()
    val successBlockHiddenLiveData = MutableLiveData<Boolean>()

    fun getBlockerTurnOn() {
        launch {
            settingsBlockerUseCase.getBlockerTurnOn().collect { blockerTurnOn ->
                blockerTurnOnLiveData.postValue(blockerTurnOn.isTrue())
            }
        }
    }

    fun setBlockerTurnOn(blockerTurnOn: Boolean) {
        launch {
            settingsBlockerUseCase.setBlockerTurnOn(blockerTurnOn)
            blockerTurnOnLiveData.postValue(blockerTurnOn)
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
        settingsBlockerUseCase.changeBlockHidden(blockHidden, application.isNetworkAvailable()) { result ->
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