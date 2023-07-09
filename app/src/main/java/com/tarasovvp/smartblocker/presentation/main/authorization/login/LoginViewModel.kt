package com.tarasovvp.smartblocker.presentation.main.authorization.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.LoginUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val application: Application,
    private val loginUseCase: LoginUseCase
) : BaseViewModel(application) {

    val isEmailAccountExistLiveData = MutableLiveData<Unit>()
    val isGoogleAccountExistLiveData = MutableLiveData<String>()
    val successPasswordResetLiveData = MutableLiveData<Boolean>()
    val successSignInLiveData = MutableLiveData<Unit>()

    fun sendPasswordResetEmail(email: String) {
        if (application.isNetworkAvailable()) {
            showProgress()
            loginUseCase.sendPasswordResetEmail(email) { authResult ->
                when(authResult) {
                    is Result.Success -> successPasswordResetLiveData.postValue(true)
                    is Result.Failure -> authResult.errorMessage?.let { exceptionLiveData.postValue(it) }
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }

    fun fetchSignInMethodsForEmail(email: String, idToken: String? = null) {
        if (application.isNetworkAvailable()) {
            showProgress()
            loginUseCase.fetchSignInMethodsForEmail(email) { authResult ->
                when(authResult) {
                    is Result.Success -> when {
                        authResult.data.isNullOrEmpty() -> {
                            idToken?.let {
                                isGoogleAccountExistLiveData.postValue(String.EMPTY)
                            }
                            exceptionLiveData.postValue(application.getString(R.string.authorization_account_not_exist))
                        }
                        idToken.isNullOrEmpty() -> isEmailAccountExistLiveData.postValue(Unit)
                        else -> idToken.let { isGoogleAccountExistLiveData.postValue(it) }
                    }
                    is Result.Failure -> authResult.errorMessage?.let { isGoogleAccountExistLiveData.postValue(it) }
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        if (application.isNetworkAvailable()) {
            showProgress()
            loginUseCase.signInWithEmailAndPassword(email, password) { authResult ->
                when(authResult) {
                    is Result.Success -> successSignInLiveData.postValue(Unit)
                    is Result.Failure -> authResult.errorMessage?.let { exceptionLiveData.postValue(it) }
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }

    fun signInAuthWithGoogle(idToken: String) {
        if (application.isNetworkAvailable()) {
            showProgress()
            loginUseCase.signInAuthWithGoogle(idToken) { operationResult ->
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

    fun signInAnonymously() {
        if (application.isNetworkAvailable()) {
            showProgress()
            loginUseCase.signInAnonymously { authResult ->
                when(authResult) {
                    is Result.Success -> successSignInLiveData.postValue(Unit)
                    is Result.Failure -> authResult.errorMessage?.let { exceptionLiveData.postValue(it) }
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }
}