package com.tarasovvp.smartblocker.usecases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils
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

    private lateinit var loginUseCase: LoginUseCase

    private val resultMock = mock<() -> Unit>()

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
        }.`when`(authRepository).sendPasswordResetEmail(eq(TestUtils.TEST_EMAIL), any())

        loginUseCase.sendPasswordResetEmail(TestUtils.TEST_EMAIL, resultMock)
        verify(resultMock).invoke()
    }

    @Test
    fun signInWithEmailAndPasswordTest() {
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[2] as () -> Unit
            result.invoke()
        }.`when`(authRepository).signInWithEmailAndPassword(eq(TestUtils.TEST_EMAIL), eq(TestUtils.TEST_PASSWORD), any())

        loginUseCase.signInWithEmailAndPassword(TestUtils.TEST_EMAIL, TestUtils.TEST_PASSWORD, resultMock)
        verify(resultMock).invoke()
    }

    @Test
    fun firebaseAuthWithGoogleTest() {
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(authRepository).signInWithGoogle(eq(TestUtils.TEST_TOKEN), any())

        loginUseCase.firebaseAuthWithGoogle(TestUtils.TEST_TOKEN, resultMock)
        verify(resultMock).invoke()
    }
}