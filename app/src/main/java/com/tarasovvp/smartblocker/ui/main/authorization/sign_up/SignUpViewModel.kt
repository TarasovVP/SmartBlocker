package com.tarasovvp.smartblocker.ui.main.authorization.sign_up

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.repository.AuthRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository
) : BaseViewModel(application) {

    val successSignInLiveData = MutableLiveData<Boolean>()

    fun createUserWithEmailAndPassword(email: String, password: String) {
        showProgress()
        authRepository.createUserWithEmailAndPassword(email, password) {
            successSignInLiveData.postValue(true)
            hideProgress()
        }
    }
}