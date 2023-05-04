package com.tarasovvp.smartblocker.presentation.main.authorization.login

import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import com.tarasovvp.smartblocker.domain.usecase.LoginUseCase

import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(private val authRepository: AuthRepository):
    LoginUseCase {

    override fun sendPasswordResetEmail(email: String, result: (OperationResult<Unit>) -> Unit) = authRepository.sendPasswordResetEmail(email) { operationResult ->
        result.invoke(operationResult)
    }

    override fun signInWithEmailAndPassword(email: String, password: String, result: (OperationResult<String?>) -> Unit) = authRepository.signInWithEmailAndPassword(email, password) { operationResult ->
        result.invoke(operationResult)
    }

    override fun firebaseAuthWithGoogle(idToken: String, result: (OperationResult<String?>) -> Unit) = authRepository.signInWithGoogle(idToken) { operationResult ->
        result.invoke(operationResult)
    }
}