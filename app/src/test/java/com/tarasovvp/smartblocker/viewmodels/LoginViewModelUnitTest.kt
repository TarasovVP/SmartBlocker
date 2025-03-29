package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_TOKEN
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.LoginUseCase
import com.tarasovvp.smartblocker.presentation.main.authorization.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelUnitTest : BaseViewModelUnitTest<LoginViewModel>() {
    @MockK
    private lateinit var useCase: LoginUseCase

    private val expectedResult = Result.Success<Unit>()

    override fun createViewModel() = LoginViewModel(application, useCase)

    @Test
    fun sendPasswordResetEmailTest() {
        every { application.isNetworkAvailable } returns true
        coEvery { useCase.sendPasswordResetEmail(eq(TEST_EMAIL), any()) } answers {
            val result = secondArg<(Result<Unit>) -> Unit>()
            result.invoke(expectedResult)
        }
        viewModel.sendPasswordResetEmail(TEST_EMAIL)
        verify { useCase.sendPasswordResetEmail(TEST_EMAIL, any()) }
        assertEquals(true, viewModel.successPasswordResetLiveData.getOrAwaitValue())
    }

    @Test
    fun signInWithEmailAndPasswordTest() {
        every { application.isNetworkAvailable } returns true
        coEvery {
            useCase.signInWithEmailAndPassword(
                eq(TEST_EMAIL),
                eq(TEST_PASSWORD),
                any(),
            )
        } answers {
            val result = thirdArg<(Result<Unit>) -> Unit>()
            result.invoke(expectedResult)
        }
        viewModel.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)
        verify { useCase.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, any()) }
        assertEquals(Unit, viewModel.successSignInLiveData.getOrAwaitValue())
    }

    @Test
    fun firebaseAuthWithGoogleTest() {
        every { application.isNetworkAvailable } returns true
        coEvery { useCase.signInAuthWithGoogle(eq(TEST_TOKEN), any()) } answers {
            val result = secondArg<(Result<Unit>) -> Unit>()
            result.invoke(expectedResult)
        }
        viewModel.signInAuthWithGoogle(TEST_TOKEN)
        verify { useCase.signInAuthWithGoogle(eq(TEST_TOKEN), any()) }
        assertEquals(Unit, viewModel.successSignInLiveData.getOrAwaitValue())
    }

    @Test
    fun signInAnonymouslyTest() {
        every { application.isNetworkAvailable } returns true
        coEvery { useCase.signInAnonymously(any()) } answers {
            val result = firstArg<(Result<Unit>) -> Unit>()
            result.invoke(expectedResult)
        }
        viewModel.signInAnonymously()
        verify { useCase.signInAnonymously(any()) }
        assertEquals(Unit, viewModel.successSignInLiveData.getOrAwaitValue())
    }
}
