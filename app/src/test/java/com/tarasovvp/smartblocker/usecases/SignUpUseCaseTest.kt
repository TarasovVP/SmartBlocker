package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up.SignUpUseCase
import com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up.SignUpUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

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
        every { authRepository.createUserWithEmailAndPassword(eq(TEST_EMAIL), eq(TEST_PASSWORD), any()) } answers {
            resultMock.invoke()
        }
        signUpUseCase.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock)
        verify { resultMock.invoke() }
    }
}