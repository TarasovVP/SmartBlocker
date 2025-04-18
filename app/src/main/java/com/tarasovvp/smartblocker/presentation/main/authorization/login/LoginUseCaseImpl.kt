package com.tarasovvp.smartblocker.presentation.main.authorization.login

import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.LoginUseCase
import javax.inject.Inject

class LoginUseCaseImpl
    @Inject
    constructor(private val authRepository: AuthRepository) :
    LoginUseCase {
        override fun sendPasswordResetEmail(
            email: String,
            result: (Result<Unit>) -> Unit,
        ) = authRepository.sendPasswordResetEmail(email) { authResult ->
            result.invoke(authResult)
        }

        override fun fetchSignInMethodsForEmail(
            email: String,
            result: (Result<List<String>>) -> Unit,
        ) = authRepository.fetchSignInMethodsForEmail(email) { authResult ->
            result.invoke(authResult)
        }

        override fun signInWithEmailAndPassword(
            email: String,
            password: String,
            result: (Result<Unit>) -> Unit,
        ) = authRepository.signInWithEmailAndPassword(email, password) { authResult ->
            result.invoke(authResult)
        }

        override fun signInAuthWithGoogle(
            idToken: String,
            result: (Result<Unit>) -> Unit,
        ) = authRepository.signInWithGoogle(idToken) { authResult ->
            result.invoke(authResult)
        }

        override fun signInAnonymously(result: (Result<Unit>) -> Unit) =
            authRepository.signInAnonymously { authResult ->
                result.invoke(authResult)
            }
    }
