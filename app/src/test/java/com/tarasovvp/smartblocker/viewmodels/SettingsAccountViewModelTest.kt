package com.tarasovvp.smartblocker.viewmodels

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.domain.usecases.SettingsAccountUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_account.SettingsAccountViewModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsAccountViewModelTest: BaseViewModelTest<SettingsAccountViewModel>() {

    @MockK
    private lateinit var useCase: SettingsAccountUseCase

    @MockK
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun createViewModel() = SettingsAccountViewModel(application, useCase)

    @Test
    fun signOutTest() {
        coEvery { useCase.signOut(eq(googleSignInClient), any()) } answers {
            val result = secondArg<() -> Unit>()
            result.invoke()
        }
        viewModel.signOut(googleSignInClient)
        assertEquals(true, viewModel.successLiveData.value)
    }

    @Test
    fun changePasswordTest() {
        val newPassword = "newPassword"
        coEvery { useCase.changePassword(eq(TEST_PASSWORD), eq(newPassword), any()) } answers {
            val result = thirdArg<() -> Unit>()
            result.invoke()
        }
        viewModel.changePassword(TEST_PASSWORD, newPassword)
        assertEquals(true, viewModel.successChangePasswordLiveData.value)
    }

    @Test
    fun deleteUserTest() {
        coEvery { useCase.deleteUser(eq(googleSignInClient), any()) } answers {
            val result = secondArg<() -> Unit>()
            result.invoke()
        }
        viewModel.deleteUser(googleSignInClient)
        assertEquals(true, viewModel.successLiveData.value)
    }
}