package com.tarasovvp.smartblocker.presentation.main.authorization.sign_up

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import com.tarasovvp.smartblocker.domain.usecase.SignUpUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    application: Application,
    private val signUpUseCase: SignUpUseCase
) : BaseViewModel(application) {

    val successSignInLiveData = MutableLiveData<Unit>()

    fun createUserWithEmailAndPassword(email: String, password: String) {
        showProgress()
        signUpUseCase.createUserWithEmailAndPassword(email, password) { operationResult ->
            when(operationResult) {
                is OperationResult.Success -> successSignInLiveData.postValue(Unit)
                is OperationResult.Failure -> operationResult.errorMessage?.let { exceptionLiveData.postValue(it) }
            }
            hideProgress()
        }
    }
}