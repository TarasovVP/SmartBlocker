package com.tarasovvp.smartblocker.presentation.main.settings.settings_account

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsAccountUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsAccountViewModel @Inject constructor(
    private val application: Application,
    private val settingsAccountUseCase: SettingsAccountUseCase
) : BaseViewModel(application) {

    val reAuthenticateLiveData = MutableLiveData<Unit>()
    val successLiveData = MutableLiveData<Unit>()
    val successChangePasswordLiveData = MutableLiveData<Unit>()

    fun signOut() {
        if (application.isNetworkAvailable()) {
            showProgress()
            settingsAccountUseCase.signOut { result ->
                when (result) {
                    is Result.Success -> successLiveData.postValue(Unit)
                    is Result.Failure -> exceptionLiveData.postValue(result.errorMessage.orEmpty())
                }
            }
            hideProgress()
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        if (application.isNetworkAvailable()) {
            showProgress()
            settingsAccountUseCase.changePassword(currentPassword, newPassword) { result ->
                when (result) {
                    is Result.Success -> successChangePasswordLiveData.postValue(Unit)
                    is Result.Failure -> exceptionLiveData.postValue(result.errorMessage.orEmpty())
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }

    fun reAuthenticate(password: String) {
        if (application.isNetworkAvailable()) {
            showProgress()
            settingsAccountUseCase.reAuthenticate(password) { result ->
                when (result) {
                    is Result.Success -> reAuthenticateLiveData.postValue(Unit)
                    is Result.Failure -> exceptionLiveData.postValue(result.errorMessage.orEmpty())
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }

    fun deleteUser() {
        if (application.isNetworkAvailable()) {
            showProgress()
            settingsAccountUseCase.deleteUser { result ->
                when (result) {
                    is Result.Success -> successLiveData.postValue(Unit)
                    is Result.Failure -> exceptionLiveData.postValue(result.errorMessage.orEmpty())
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }
}