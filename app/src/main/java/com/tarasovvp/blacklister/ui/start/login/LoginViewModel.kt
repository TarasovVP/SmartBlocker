package com.tarasovvp.blacklister.ui.start.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.repository.AuthRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class LoginViewModel(application: Application) : BaseViewModel(application) {

    private val authRepository = AuthRepository

    val successPasswordResetLiveData = MutableLiveData<Boolean>()
    val successSignInLiveData = MutableLiveData<Boolean>()

    fun sendPasswordResetEmail(email: String) {
        authRepository.sendPasswordResetEmail(email) {
            successPasswordResetLiveData.postValue(true)
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        authRepository.signInWithEmailAndPassword(email, password) {
            successSignInLiveData.postValue(true)
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        authRepository.firebaseAuthWithGoogle(idToken) {
            successSignInLiveData.postValue(true)
        }
    }
}