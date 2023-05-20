package com.tarasovvp.smartblocker.presentation.main.settings.settings_account

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsAccountUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsAccountViewModel @Inject constructor(
    private val application: Application,
    private val settingsAccountUseCase: SettingsAccountUseCase
) : BaseViewModel(application) {

    val successLiveData = MutableLiveData<Unit>()
    val successChangePasswordLiveData = MutableLiveData<Unit>()

    fun signOut() {
        if ((application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) {
            showProgress()
            settingsAccountUseCase.signOut { result ->
                when (result) {
                    is Result.Success -> successLiveData.postValue(Unit)
                    is Result.Failure -> exceptionLiveData.postValue(result.errorMessage)
                }
            }
            hideProgress()
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        if ((application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) {
            showProgress()
            settingsAccountUseCase.changePassword(currentPassword, newPassword) { result ->
                when (result) {
                    is Result.Success -> successChangePasswordLiveData.postValue(Unit)
                    is Result.Failure -> exceptionLiveData.postValue(result.errorMessage)
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }

    fun deleteUser() {
        if ((application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) {
            showProgress()
            settingsAccountUseCase.deleteUser { result ->
                when (result) {
                    is Result.Success -> successLiveData.postValue(Unit)
                    is Result.Failure -> exceptionLiveData.postValue(result.errorMessage)
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }
}