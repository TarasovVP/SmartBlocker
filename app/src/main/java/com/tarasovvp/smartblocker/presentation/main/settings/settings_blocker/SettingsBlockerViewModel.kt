package com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsBlockerViewModel @Inject constructor(
    private val application: Application,
    private val settingsBlockerUseCase: SettingsBlockerUseCase,
    private val countryCodeUIMapper: CountryCodeUIMapper
) : BaseViewModel(application) {

    val blockerTurnOnLiveData = MutableLiveData<Boolean>()
    val blockHiddenLiveData = MutableLiveData<Boolean>()
    val successBlockHiddenLiveData = MutableLiveData<Boolean>()
    val currentCountryCodeLiveData = MutableLiveData<CountryCodeUIModel>()
    val successCurrentCountryCodeLiveData = MutableLiveData<CountryCodeUIModel>()

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
            blockerTurnOnLiveData.postValue(blockerTurnOn.not())
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
                currentCountryCodeLiveData.postValue(countryCode.takeIf { it.isNotNull() }?.let { countryCodeUIMapper.mapToUIModel(it) } ?: CountryCodeUIModel())
            }
        }
    }

    fun setCurrentCountryCode(countryCode: CountryCodeUIModel) {
        launch {
            settingsBlockerUseCase.setCurrentCountryCode(countryCodeUIMapper.mapFromUIModel(countryCode))
            successCurrentCountryCodeLiveData.postValue(countryCode)
        }
    }
}