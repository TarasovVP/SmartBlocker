package com.tarasovvp.smartblocker.domain.usecase.authorization.login

import com.tarasovvp.smartblocker.domain.repository.AuthRepository

import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(private val authRepository: AuthRepository): LoginUseCase  {

    override fun sendPasswordResetEmail(email: String, result: () -> Unit) = authRepository.sendPasswordResetEmail(email) {
        result.invoke()
    }

    override fun signInWithEmailAndPassword(email: String, password: String, result: () -> Unit) = authRepository.signInWithEmailAndPassword(email, password) {
        result.invoke()
    }

    override fun firebaseAuthWithGoogle(idToken: String, result: () -> Unit) = authRepository.signInWithGoogle(idToken) {
        result.invoke()
    }
}