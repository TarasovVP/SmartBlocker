package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.usecase.authorization.login.LoginUseCaseImpl
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var loginUseCaseImpl: LoginUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        loginUseCaseImpl = LoginUseCaseImpl(authRepository)
    }

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