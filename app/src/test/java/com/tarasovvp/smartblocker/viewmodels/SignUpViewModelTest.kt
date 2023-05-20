package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.domain.usecases.SignUpUseCase
import com.tarasovvp.smartblocker.presentation.main.authorization.sign_up.SignUpViewModel
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class SignUpViewModelTest: BaseViewModelTest<SignUpViewModel>() {

    @MockK
    private lateinit var useCase: SignUpUseCase

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    override fun createViewModel() = SignUpViewModel(application, useCase)

    @Test
    fun createUserWithEmailAndPasswordTest() {
        coEvery { useCase.createUserWithEmailAndPassword(eq(TEST_EMAIL), eq(TEST_PASSWORD), eq(resultMock)) } answers {
            resultMock.invoke(Result.Success())
        }
        viewModel.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)
        coVerify { useCase.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock) }
        verify { resultMock.invoke(Result.Success()) }
        verify { viewModel.successSignInLiveData.postValue(Unit) }
    }
}