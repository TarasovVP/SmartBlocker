package com.tarasovvp.smartblocker.presentation.main.authorization.sign_up

import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import com.tarasovvp.smartblocker.domain.usecase.SignUpUseCase
import javax.inject.Inject

class SignUpUseCaseImpl @Inject constructor(private val authRepository: AuthRepository) :
    SignUpUseCase {

    override fun createUserWithEmailAndPassword(email: String, password: String, result: (OperationResult<String?>) -> Unit) = authRepository.createUserWithEmailAndPassword(email, password) { operationResult ->
        result.invoke(operationResult)
    }
}