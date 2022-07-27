package com.tarasovvp.blacklister.ui.settings.settingslist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.repository.AuthRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class SettingsListViewModel(application: Application) : BaseViewModel(application) {

    private val authRepository = AuthRepository

    val successLiveData = MutableLiveData<Boolean>()

    fun signOut() {
        showProgress()
        authRepository.signOut {
            successLiveData.postValue(true)
            hideProgress()
        }
    }
}