package com.tarasovvp.smartblocker.ui.settings.settings_privacy

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.models.CountryCode
import com.tarasovvp.smartblocker.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel

class SettingsPrivacyViewModel(application: Application) : BaseViewModel(application) {

    private val realDataBaseRepository = RealDataBaseRepository

    val successBlockHiddenLiveData = MutableLiveData<Boolean>()

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
}