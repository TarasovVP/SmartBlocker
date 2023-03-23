package com.tarasovvp.smartblocker.ui.main.settings.settings_account

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.repository.AuthRepository
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

    fun signOut(googleSignInClient: GoogleSignInClient) {
        showProgress()
        authRepository.signOut(googleSignInClient) {
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

    fun deleteUser(googleSignInClient: GoogleSignInClient) {
        showProgress()
        authRepository.deleteUser(googleSignInClient) {
            successLiveData.postValue(true)
            hideProgress()
        }
    }
}