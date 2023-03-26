package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up.SignUpUseCaseImpl
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

//TODO unfinished
@Suppress
@RunWith(MockitoJUnitRunner::class)
class SignUpUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var signUpUseCaseImpl: SignUpUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        signUpUseCaseImpl = SignUpUseCaseImpl(authRepository)
    }
    fun createUserWithEmailAndPassword(email: String, password: String, result: () -> Unit, ) = authRepository.createUserWithEmailAndPassword(email, password) {
        result.invoke()
    }
}