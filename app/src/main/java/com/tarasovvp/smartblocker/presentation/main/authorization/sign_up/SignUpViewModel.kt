package com.tarasovvp.smartblocker.presentation.main.authorization.sign_up

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SignUpUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val application: Application,
    private val signUpUseCase: SignUpUseCase
) : BaseViewModel(application) {

    val successSignInLiveData = MutableLiveData<Unit>()

    fun createUserWithEmailAndPassword(email: String, password: String) {
        if ((application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) {
            showProgress()
            signUpUseCase.createUserWithEmailAndPassword(email, password) { operationResult ->
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
}