package com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.domain.usecase.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsBlockerViewModel @Inject constructor(
    application: Application,
    private val settingsBlockerUseCase: SettingsBlockerUseCase
) : BaseViewModel(application) {

    val successBlockHiddenLiveData = MutableLiveData<Boolean>()

    fun changeBlockHidden(blockHidden: Boolean) {
        showProgress()
        if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
            settingsBlockerUseCase.changeBlockHidden(blockHidden) {
                successBlockHiddenLiveData.postValue(blockHidden.not())
            }
        } else {
            successBlockHiddenLiveData.postValue(blockHidden)
        }
        hideProgress()
    }
}