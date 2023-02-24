package com.tarasovvp.smartblocker.ui.main.settings.settings_account

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.repository.interfaces.AuthRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsAccountViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository
) : BaseViewModel(application) {

    val successLiveData = MutableLiveData<Boolean>()
    val successChangePasswordLiveData = MutableLiveData<Boolean>()

    fun signOut() {
        showProgress()
        authRepository.signOut {
            successLiveData.postValue(true)
            hideProgress()
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        showProgress()
        authRepository.changePassword(currentPassword, newPassword) {
            SmartBlockerApp.instance?.auth = null
            successChangePasswordLiveData.postValue(true)
            hideProgress()
        }
    }

    fun deleteUser() {
        showProgress()
        authRepository.deleteUser {
            successLiveData.postValue(true)
            hideProgress()
        }
    }
}