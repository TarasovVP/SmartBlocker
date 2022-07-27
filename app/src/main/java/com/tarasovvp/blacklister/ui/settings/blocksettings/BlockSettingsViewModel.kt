package com.tarasovvp.blacklister.ui.settings.blocksettings

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.repository.RealDataBaseRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class BlockSettingsViewModel(application: Application) : BaseViewModel(application) {

    private val realDataBaseRepository = RealDataBaseRepository

    val successPriorityLiveData = MutableLiveData<Boolean>()
    val successBlockHiddenLiveData = MutableLiveData<Boolean>()

    fun changePriority(whiteListPriority: Boolean) {
        showProgress()
        launch {
            if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
                realDataBaseRepository.changeWhiteListPriority(whiteListPriority) {
                    successPriorityLiveData.postValue(whiteListPriority)
                }
            } else {
                SharedPreferencesUtil.isWhiteListPriority = whiteListPriority
            }
            hideProgress()
        }
    }

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