package com.tarasovvp.smartblocker.presentation.main.authorization.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.usecase.LoginUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val loginUseCase: LoginUseCase
) : BaseViewModel(application) {

    val successPasswordResetLiveData = MutableLiveData<Boolean>()
    val successSignInLiveData = MutableLiveData<Boolean>()

    fun sendPasswordResetEmail(email: String) {
        showProgress()
        loginUseCase.sendPasswordResetEmail(email) {
            successPasswordResetLiveData.postValue(true)
            hideProgress()
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        showProgress()
        loginUseCase.signInWithEmailAndPassword(email, password) {
            successSignInLiveData.postValue(true)
            hideProgress()
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        showProgress()
        loginUseCase.firebaseAuthWithGoogle(idToken) {
            successSignInLiveData.postValue(true)
            hideProgress()
        }
    }
}