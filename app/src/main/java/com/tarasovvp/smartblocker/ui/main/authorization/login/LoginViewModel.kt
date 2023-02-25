package com.tarasovvp.smartblocker.ui.main.authorization.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.repository.AuthRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository
) : BaseViewModel(application) {

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
        authRepository.signInWithGoogle(idToken) {
            successSignInLiveData.postValue(true)
            hideProgress()
        }
    }
}