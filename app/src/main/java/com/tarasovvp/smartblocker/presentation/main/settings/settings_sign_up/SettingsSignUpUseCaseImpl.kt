package com.tarasovvp.smartblocker.presentation.main.settings.settings_sign_up

import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SignUpUseCase
import javax.inject.Inject

class SettingsSignUpUseCaseImpl @Inject constructor(private val authRepository: AuthRepository) :
    SignUpUseCase {

    override fun createUserWithEmailAndPassword(email: String, password: String, result: (Result<Unit>) -> Unit) = authRepository.createUserWithEmailAndPassword(email, password) { authResult ->
        result.invoke(authResult)
    }
}