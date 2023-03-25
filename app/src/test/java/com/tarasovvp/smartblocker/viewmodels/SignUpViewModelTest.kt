package com.tarasovvp.smartblocker.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up.SignUpUseCase
import com.tarasovvp.smartblocker.presentation.main.authorization.sign_up.SignUpViewModel
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SignUpViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var signUpUseCase: SignUpUseCase

    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel =
            SignUpViewModel(application, signUpUseCase)
    }

    @Test
    fun createUserWithEmailAndPasswordTest() {
        val email = "email"
        val password = "password"
        Mockito.doAnswer {
            val result = it.arguments[2] as () -> Unit
            result.invoke()
        }.`when`(signUpUseCase).createUserWithEmailAndPassword(eq(email), eq(password), any())
        viewModel.createUserWithEmailAndPassword(email, password)
        assertEquals(true, viewModel.successSignInLiveData.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}