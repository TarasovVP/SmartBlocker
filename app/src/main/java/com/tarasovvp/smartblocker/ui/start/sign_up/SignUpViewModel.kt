package com.tarasovvp.smartblocker.ui.start.sign_up

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.repository.AuthRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel

class SignUpViewModel(application: Application) : BaseViewModel(application) {

    private val authRepository = AuthRepository

    val successSignInLiveData = MutableLiveData<Boolean>()

    fun createUserWithEmailAndPassword(email: String, password: String) {
        showProgress()
        authRepository.createUserWithEmailAndPassword(email, password) {
            successSignInLiveData.postValue(true)
            hideProgress()
        }
    }
}