package com.tarasovvp.blacklister.ui.main.settings.blocksettings

import android.app.Application
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.repository.RealDataBaseRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class BlockSettingsViewModel(application: Application) : BaseViewModel(application) {

    private val realDataBaseRepository = RealDataBaseRepository

    fun changePriority(whiteListPriority: Boolean) {
        showProgress()
        launch {
            if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
                realDataBaseRepository.changeWhiteListPriority(whiteListPriority) {
                    SharedPreferencesUtil.isWhiteListPriority = whiteListPriority
                }
            }
            hideProgress()
        }
    }

    fun changeBlockAnonymous(blockUnanimous: Boolean) {
        showProgress()
        launch {
            if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
                realDataBaseRepository.changeBlockAnonymous(blockUnanimous) {
                    SharedPreferencesUtil.blockHidden = blockUnanimous
                }
            } else {
                SharedPreferencesUtil.blockHidden = blockUnanimous
            }
            hideProgress()
        }
    }
}