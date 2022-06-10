package com.tarasovvp.blacklister.ui.main.settings.settingslist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class SettingsListViewModel(application: Application) : BaseViewModel(application) {
    val successLiveData = MutableLiveData<Boolean>()

    fun signOut() {
        try {
            BlackListerApp.instance?.auth?.signOut()
            successLiveData.postValue(true)
        } catch (e: Exception) {
            exceptionLiveData.postValue(e.localizedMessage)
        }
    }
}