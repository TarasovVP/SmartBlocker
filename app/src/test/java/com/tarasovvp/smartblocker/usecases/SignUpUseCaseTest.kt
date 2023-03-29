package com.tarasovvp.smartblocker.usecases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.TestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up.SignUpUseCase
import com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up.SignUpUseCaseImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignUpUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var signUpUseCase: SignUpUseCase

    private val resultMock = mock<() -> Unit>()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        signUpUseCase = SignUpUseCaseImpl(authRepository)
    }

    @Test
    fun createUserWithEmailAndPasswordTest() {
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[2] as () -> Unit
            result.invoke()
        }.`when`(authRepository).createUserWithEmailAndPassword(eq(TEST_EMAIL), eq(TEST_PASSWORD), any())
        signUpUseCase.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock)
        verify(resultMock).invoke()
    }
}