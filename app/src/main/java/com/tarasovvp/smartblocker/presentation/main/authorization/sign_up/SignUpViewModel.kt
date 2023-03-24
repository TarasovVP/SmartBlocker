package com.tarasovvp.smartblocker.presentation.main.authorization.sign_up

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up.SignUpUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    application: Application,
    private val signUpUseCase: SignUpUseCase
) : BaseViewModel(application) {

    val successSignInLiveData = MutableLiveData<Boolean>()

    fun createUserWithEmailAndPassword(email: String, password: String) {
        showProgress()
        signUpUseCase.createUserWithEmailAndPassword(email, password) {
            successSignInLiveData.postValue(true)
            hideProgress()
        }
    }
}