package com.tarasovvp.blacklister.ui.start.sign_up

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.repository.AuthRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class SignUpViewModel(application: Application) : BaseViewModel(application) {

    private val authRepository = AuthRepository

    val successSignInLiveData = MutableLiveData<Boolean>()

    fun createUserWithEmailAndPassword(email: String, password: String, name: String) {
        showProgress()
        authRepository.createUserWithEmailAndPassword(email, password, name) {
            successSignInLiveData.postValue(true)
            hideProgress()
        }
    }
}