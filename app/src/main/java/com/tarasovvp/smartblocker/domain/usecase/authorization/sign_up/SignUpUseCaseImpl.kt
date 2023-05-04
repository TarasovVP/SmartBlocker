package com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up

import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCaseImpl @Inject constructor(private val authRepository: AuthRepository) : SignUpUseCase {

    override fun createUserWithEmailAndPassword(email: String, password: String, result: (String?) -> Unit, ) = authRepository.createUserWithEmailAndPassword(email, password) {
        result.invoke(it)
    }
}