package com.tarasovvp.smartblocker.ui.start.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.repository.AuthRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel

class LoginViewModel(application: Application) : BaseViewModel(application) {

    private val authRepository = AuthRepository

    val successPasswordResetLiveData = MutableLiveData<Boolean>()
    val successSignInLiveData = MutableLiveData<Boolean>()

    fun sendPasswordResetEmail(email: String) {
        showProgress()
        authRepository.sendPasswordResetEmail(email) {
            successPasswordResetLiveData.postValue(true)
            hideProgress()
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        showProgress()
        authRepository.signInWithEmailAndPassword(email, password) {
            successSignInLiveData.postValue(true)
            hideProgress()
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        showProgress()
        authRepository.firebaseAuthWithGoogle(idToken) {
            successSignInLiveData.postValue(true)
            hideProgress()
        }
    }
}