package com.tarasovvp.smartblocker.usecases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up.SignUpUseCase
import com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up.SignUpUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class SignUpUseCaseTest {

    @MockK
    private lateinit var authRepository: AuthRepository

    @MockK(relaxed = true)
    private lateinit var resultMock: () -> Unit

    private lateinit var signUpUseCase: SignUpUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
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