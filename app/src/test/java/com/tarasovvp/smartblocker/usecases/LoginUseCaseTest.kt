package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.usecase.authorization.login.LoginUseCase
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class LoginUseCaseTest @Inject constructor(private val authRepository: AuthRepository) {

    fun sendPasswordResetEmail(email: String, result: () -> Unit) = authRepository.sendPasswordResetEmail(email) {
        result.invoke()
    }

    fun signInWithEmailAndPassword(email: String, password: String, result: () -> Unit) = authRepository.signInWithEmailAndPassword(email, password) {
        result.invoke()
    }

    fun firebaseAuthWithGoogle(idToken: String, result: () -> Unit) = authRepository.signInWithGoogle(idToken) {
        result.invoke()
    }
}