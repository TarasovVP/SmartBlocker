package com.tarasovvp.smartblocker.ui.main.settings.settings_blocker

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel

class SettingsBlockerViewModel(application: Application) : BaseViewModel(application) {

    private val realDataBaseRepository = RealDataBaseRepository

    val successBlockHiddenLiveData = MutableLiveData<Boolean>()

    fun changeBlockHidden(blockHidden: Boolean) {
        showProgress()
        launch ({ throwable, _ ->
            exceptionLiveData.postValue(throwable.localizedMessage)
            successBlockHiddenLiveData.postValue(blockHidden.not())
            hideProgress()
        }, {
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                realDataBaseRepository.changeBlockHidden(blockHidden) {
                    successBlockHiddenLiveData.postValue(blockHidden)
                }
            } else {
                successBlockHiddenLiveData.postValue(blockHidden)
            }
            hideProgress()
        })
    }
}