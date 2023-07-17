package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SignUpUseCase
import com.tarasovvp.smartblocker.presentation.main.authorization.sign_up.SignUpUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SignUpUseCaseUnitTest {

    @MockK
    private lateinit var authRepository: AuthRepository

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<String>) -> Unit

    private lateinit var signUpUseCase: SignUpUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        signUpUseCase = SignUpUseCaseImpl(authRepository, realDataBaseRepository)
    }

    @Test
    fun createUserWithEmailAndPasswordTest() {
        every { authRepository.createUserWithEmailAndPassword(eq(TEST_EMAIL), eq(TEST_PASSWORD), any()) } answers {
            resultMock.invoke(Result.Success())
        }
        signUpUseCase.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock)
        verify { resultMock.invoke(Result.Success()) }
    }
}