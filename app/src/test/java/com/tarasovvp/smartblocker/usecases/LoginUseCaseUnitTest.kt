package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.LoginUseCase
import com.tarasovvp.smartblocker.presentation.main.authorization.login.LoginUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LoginUseCaseUnitTest {

    @MockK
    private lateinit var authRepository: AuthRepository

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        loginUseCase = LoginUseCaseImpl(authRepository)
    }

    @Test
    fun sendPasswordResetEmailTest() {
        every { authRepository.sendPasswordResetEmail(eq(UnitTestUtils.TEST_EMAIL), any()) } answers {
            resultMock.invoke(Result.Success())
        }
        loginUseCase.sendPasswordResetEmail(UnitTestUtils.TEST_EMAIL, resultMock)
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun signInWithEmailAndPasswordTest() {
        every { authRepository.signInWithEmailAndPassword(eq(UnitTestUtils.TEST_EMAIL), eq(UnitTestUtils.TEST_PASSWORD), any()) } answers {
            resultMock.invoke(Result.Success())
        }
        loginUseCase.signInWithEmailAndPassword(UnitTestUtils.TEST_EMAIL, UnitTestUtils.TEST_PASSWORD, resultMock)
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun firebaseAuthWithGoogleTest() {
        every { authRepository.signInWithGoogle(eq(UnitTestUtils.TEST_TOKEN), any()) } answers {
            resultMock.invoke(Result.Success())
        }
        loginUseCase.firebaseAuthWithGoogle(UnitTestUtils.TEST_TOKEN, resultMock)
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun signInAnonymouslyTest() {
        every { authRepository.signInAnonymously(any()) } answers {
            resultMock.invoke(Result.Success())
        }
        loginUseCase.signInAnonymously(resultMock)
        verify { resultMock.invoke(Result.Success()) }
    }
}