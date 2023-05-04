package com.tarasovvp.smartblocker.presentation.main.settings.settings_account

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.usecase.SettingsAccountUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsAccountViewModel @Inject constructor(
    application: Application,
    private val settingsAccountUseCase: SettingsAccountUseCase
) : BaseViewModel(application) {

    val successLiveData = MutableLiveData<Boolean>()
    val successChangePasswordLiveData = MutableLiveData<Boolean>()

    fun signOut(googleSignInClient: GoogleSignInClient) {
        showProgress()
        settingsAccountUseCase.signOut(googleSignInClient) {
            successLiveData.postValue(true)
            hideProgress()
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        showProgress()
        settingsAccountUseCase.changePassword(currentPassword, newPassword) {
            SmartBlockerApp.instance?.firebaseAuth = null
            successChangePasswordLiveData.postValue(true)
            hideProgress()
        }
    }

    fun deleteUser(googleSignInClient: GoogleSignInClient) {
        showProgress()
        settingsAccountUseCase.deleteUser(googleSignInClient) {
            successLiveData.postValue(true)
            hideProgress()
        }
    }
}