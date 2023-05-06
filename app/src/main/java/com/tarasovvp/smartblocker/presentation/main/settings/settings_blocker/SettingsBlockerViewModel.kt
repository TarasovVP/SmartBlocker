package com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
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

    val successBlockHiddenLiveData = MutableLiveData<Boolean>()

    fun changeBlockHidden(blockHidden: Boolean) {
        showProgress()
        settingsBlockerUseCase.changeBlockHidden(blockHidden, (application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) { result ->
            when(result) {
                is Result.Success -> successBlockHiddenLiveData.postValue(blockHidden)
                is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
            }
        }
        hideProgress()
    }
}