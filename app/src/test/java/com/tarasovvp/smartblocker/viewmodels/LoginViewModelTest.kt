package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_TOKEN
import com.tarasovvp.smartblocker.domain.usecases.LoginUseCase
import com.tarasovvp.smartblocker.presentation.main.authorization.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest: BaseViewModelTest<LoginViewModel>() {

    @MockK
    private lateinit var useCase: LoginUseCase

    override fun createViewModel() = LoginViewModel(application, useCase)

    @Test
    fun sendPasswordResetEmailTest() {
        coEvery { useCase.sendPasswordResetEmail(eq(TEST_EMAIL), any()) } answers {
            val result = secondArg<() -> Unit>()
            result.invoke()
        }
        viewModel.sendPasswordResetEmail(TEST_EMAIL)
        assertTrue(viewModel.successPasswordResetLiveData.value == true)
    }

    @Test
    fun signInWithEmailAndPasswordTest() {
        coEvery { useCase.signInWithEmailAndPassword(eq(TEST_EMAIL), eq(TEST_PASSWORD), any()) } answers {
            val result = thirdArg<() -> Unit>()
            result.invoke()
        }
        viewModel.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)
        assertTrue(viewModel.successSignInLiveData.value == true)
    }

    @Test
    fun firebaseAuthWithGoogleTest() {
        coEvery { useCase.firebaseAuthWithGoogle(eq(TEST_TOKEN), any()) } answers {
            val result = secondArg<() -> Unit>()
            result.invoke()
        }
        viewModel.firebaseAuthWithGoogle(TEST_TOKEN)
        assertTrue(viewModel.successSignInLiveData.value == true)
    }
}