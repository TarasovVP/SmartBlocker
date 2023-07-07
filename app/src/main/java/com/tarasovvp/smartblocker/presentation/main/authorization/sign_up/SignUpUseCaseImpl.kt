package com.tarasovvp.smartblocker.presentation.main.authorization.sign_up

import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SignUpUseCase
import javax.inject.Inject

class SignUpUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val realDataBaseRepository: RealDataBaseRepository): SignUpUseCase {

    override fun createUserWithEmailAndPassword(email: String, password: String, result: (Result<String>) -> Unit) = authRepository.createUserWithEmailAndPassword(email, password) { authResult ->
        result.invoke(authResult)
    }

    override fun createCurrentUser(currentUser: CurrentUser, result: (Result<Unit>) -> Unit) = realDataBaseRepository.createCurrentUser(currentUser) { authResult ->
        result.invoke(authResult)
    }
}