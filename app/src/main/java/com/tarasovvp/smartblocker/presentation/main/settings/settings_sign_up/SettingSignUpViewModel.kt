package com.tarasovvp.smartblocker.presentation.main.settings.settings_sign_up

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingSignUpViewModel @Inject constructor(
    private val application: Application,
    private val settingsSignUpUseCaseImpl: SettingsSignUpUseCaseImpl
) : BaseViewModel(application) {

    val successSignInLiveData = MutableLiveData<Unit>()

    fun createUserWithEmailAndPassword(email: String, password: String) {
        if (application.isNetworkAvailable()) {
            showProgress()
            settingsSignUpUseCaseImpl.createUserWithEmailAndPassword(email, password) { operationResult ->
                when(operationResult) {
                    is Result.Success -> successSignInLiveData.postValue(Unit)
                    is Result.Failure -> operationResult.errorMessage?.let { exceptionLiveData.postValue(it) }
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }
}