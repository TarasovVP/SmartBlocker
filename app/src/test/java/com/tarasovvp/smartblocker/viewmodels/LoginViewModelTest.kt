package com.tarasovvp.smartblocker.viewmodels

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.TestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.TestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.TestUtils.TEST_TOKEN
import com.tarasovvp.smartblocker.domain.usecase.authorization.login.LoginUseCase
import com.tarasovvp.smartblocker.presentation.main.authorization.login.LoginViewModel
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest: BaseViewModelTest<LoginViewModel>() {

    @Mock
    private lateinit var useCase: LoginUseCase

    override fun createViewModel() = LoginViewModel(application, useCase)

    @Test
    fun sendPasswordResetEmailTest() {
        doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(useCase).sendPasswordResetEmail(eq(TEST_EMAIL), any())

        viewModel.sendPasswordResetEmail(TEST_EMAIL)
        assertTrue(viewModel.successPasswordResetLiveData.value == true)
    }

    @Test
    fun signInWithEmailAndPasswordTest() {
        doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[2] as () -> Unit
            result.invoke()
        }.`when`(useCase).signInWithEmailAndPassword(eq(TEST_EMAIL), eq(TEST_PASSWORD), any())

        viewModel.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)
        assertTrue(viewModel.successSignInLiveData.value == true)
    }

    @Test
    fun firebaseAuthWithGoogleTest() {
        doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(useCase).firebaseAuthWithGoogle(eq(TEST_TOKEN), any())

        viewModel.firebaseAuthWithGoogle(TEST_TOKEN)
        assertTrue(viewModel.successSignInLiveData.value == true)
    }
}