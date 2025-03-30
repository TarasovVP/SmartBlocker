package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.SignUpUseCase
import com.tarasovvp.smartblocker.presentation.main.authorization.signup.SignUpViewModel
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class SignUpViewModelUnitTest : BaseViewModelUnitTest<SignUpViewModel>() {
    @MockK
    private lateinit var useCase: SignUpUseCase

    override fun createViewModel() = SignUpViewModel(application, useCase)

    @Test
    fun createUserWithEmailAndPasswordTest() {
        val expectedResult = Result.Success<Unit>()
        every { application.isNetworkAvailable } returns true
        every {
            useCase.createUserWithEmailAndPassword(
                eq(TEST_EMAIL),
                eq(TEST_PASSWORD),
                any(),
            )
        } answers {
            val callback = thirdArg<(Result<Unit>) -> Unit>()
            callback.invoke(expectedResult)
        }
        viewModel.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)
        verify { useCase.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, any()) }
        assertEquals(Unit, viewModel.successSignUpLiveData.getOrAwaitValue())
    }
}
