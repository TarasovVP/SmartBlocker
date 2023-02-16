package com.tarasovvp.smartblocker.ui.main.settings.settings_blocker

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsBlockerViewModel @Inject constructor(
    application: Application,
    private val realDataBaseRepository: RealDataBaseRepository
) : BaseViewModel(application) {

    val successBlockHiddenLiveData = MutableLiveData<Boolean>()

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
}