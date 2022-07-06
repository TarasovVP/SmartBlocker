package com.tarasovvp.blacklister.ui.main.settings.blocksettings

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.repository.RealDataBaseRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class BlockSettingsViewModel(application: Application) : BaseViewModel(application) {

    private val realDataBaseRepository = RealDataBaseRepository
    val isWhiteListPriorityLiveData = MutableLiveData<Boolean>()

    fun changePriority(whiteListPriority: Boolean) {
        viewModelScope.launch {
            try {
                if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
                    realDataBaseRepository.changeWhiteListPriority(whiteListPriority) {
                        isWhiteListPriorityLiveData.postValue(true)
                    }
                } else {
                    SharedPreferencesUtil.isWhiteListPriority = whiteListPriority
                }
            } catch (e: Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }
}