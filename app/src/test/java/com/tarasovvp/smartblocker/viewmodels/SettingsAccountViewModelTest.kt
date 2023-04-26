package com.tarasovvp.smartblocker.viewmodels

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_account.SettingsAccountUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_account.SettingsAccountViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SettingsAccountViewModelTest: BaseViewModelTest<SettingsAccountViewModel>() {

    @Mock
    private lateinit var useCase: SettingsAccountUseCase

    @Mock
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun createViewModel() = SettingsAccountViewModel(application, useCase)

    @Test
    fun signOutTest() {
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(useCase).signOut(eq(googleSignInClient), any())
        viewModel.signOut(googleSignInClient)
        assertEquals(true, viewModel.successLiveData.value)
    }

    @Test
    fun changePasswordTest() {
        val newPassword = "newPassword"
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[2] as () -> Unit
            result.invoke()
        }.`when`(useCase).changePassword(eq(TEST_PASSWORD), eq(newPassword), any())
        viewModel.changePassword(TEST_PASSWORD, newPassword)
        assertEquals(true, viewModel.successChangePasswordLiveData.value)
    }

    @Test
    fun deleteUserTest() {
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(useCase).deleteUser(eq(googleSignInClient), any())
        viewModel.deleteUser(googleSignInClient)
        assertEquals(true, viewModel.successLiveData.value)
    }
}