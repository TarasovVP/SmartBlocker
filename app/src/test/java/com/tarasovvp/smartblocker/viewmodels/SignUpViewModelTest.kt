package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.domain.usecase.SignUpUseCase
import com.tarasovvp.smartblocker.presentation.main.authorization.sign_up.SignUpViewModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class SignUpViewModelTest: BaseViewModelTest<SignUpViewModel>() {

    @MockK
    private lateinit var useCase: SignUpUseCase

    override fun createViewModel() = SignUpViewModel(application, useCase)

    @Test
    fun createUserWithEmailAndPasswordTest() {
        coEvery { useCase.createUserWithEmailAndPassword(eq(TEST_EMAIL), eq(TEST_PASSWORD), any()) } answers {
            val result = thirdArg<() -> Unit>()
            result.invoke()
        }
        viewModel.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)
        assertEquals(true, viewModel.successSignInLiveData.value)
    }
}