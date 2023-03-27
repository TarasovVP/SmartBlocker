package com.tarasovvp.smartblocker.viewmodels

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up.SignUpUseCase
import com.tarasovvp.smartblocker.presentation.main.authorization.sign_up.SignUpViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SignUpViewModelTest: BaseViewModelTest<SignUpViewModel>() {

    @Mock
    private lateinit var useCase: SignUpUseCase

    override fun createViewModel() = SignUpViewModel(application, useCase)

    @Test
    fun createUserWithEmailAndPasswordTest() {
        val email = "email"
        val password = "password"
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[2] as () -> Unit
            result.invoke()
        }.`when`(useCase).createUserWithEmailAndPassword(eq(email), eq(password), any())
        viewModel.createUserWithEmailAndPassword(email, password)
        assertEquals(true, viewModel.successSignInLiveData.value)
    }
}