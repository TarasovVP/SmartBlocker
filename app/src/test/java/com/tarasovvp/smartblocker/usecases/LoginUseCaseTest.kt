package com.tarasovvp.smartblocker.usecases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.UnitTestUtils
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.usecase.authorization.login.LoginUseCase
import com.tarasovvp.smartblocker.domain.usecase.authorization.login.LoginUseCaseImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var resultMock: () -> Unit

    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        loginUseCase = LoginUseCaseImpl(authRepository)
    }

    @Test
    fun sendPasswordResetEmailTest() {
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(authRepository).sendPasswordResetEmail(eq(UnitTestUtils.TEST_EMAIL), any())

        loginUseCase.sendPasswordResetEmail(UnitTestUtils.TEST_EMAIL, resultMock)
        verify(resultMock).invoke()
    }

    @Test
    fun signInWithEmailAndPasswordTest() {
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[2] as () -> Unit
            result.invoke()
        }.`when`(authRepository).signInWithEmailAndPassword(eq(UnitTestUtils.TEST_EMAIL), eq(UnitTestUtils.TEST_PASSWORD), any())

        loginUseCase.signInWithEmailAndPassword(UnitTestUtils.TEST_EMAIL, UnitTestUtils.TEST_PASSWORD, resultMock)
        verify(resultMock).invoke()
    }

    @Test
    fun firebaseAuthWithGoogleTest() {
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(authRepository).signInWithGoogle(eq(UnitTestUtils.TEST_TOKEN), any())

        loginUseCase.firebaseAuthWithGoogle(UnitTestUtils.TEST_TOKEN, resultMock)
        verify(resultMock).invoke()
    }
}