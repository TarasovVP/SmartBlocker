package com.tarasovvp.smartblocker.presentation.main.authorization.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import com.tarasovvp.smartblocker.domain.usecase.LoginUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val application: Application,
    private val loginUseCase: LoginUseCase
) : BaseViewModel(application) {

    val successPasswordResetLiveData = MutableLiveData<Boolean>()
    val successSignInLiveData = MutableLiveData<Unit>()

    fun sendPasswordResetEmail(email: String) {
        if ((application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) {
            showProgress()
            loginUseCase.sendPasswordResetEmail(email) {
                successPasswordResetLiveData.postValue(true)
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        if ((application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) {
            showProgress()
            loginUseCase.signInWithEmailAndPassword(email, password) { operationResult ->
                when(operationResult) {
                    is OperationResult.Success -> successSignInLiveData.postValue(Unit)
                    is OperationResult.Failure -> operationResult.errorMessage?.let { exceptionLiveData.postValue(it) }
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        if ((application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) {
            showProgress()
            loginUseCase.firebaseAuthWithGoogle(idToken) { operationResult ->
                when(operationResult) {
                    is OperationResult.Success -> successSignInLiveData.postValue(Unit)
                    is OperationResult.Failure -> operationResult.errorMessage?.let { exceptionLiveData.postValue(it) }
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }
}