package com.tarasovvp.smartblocker.viewmodels

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.domain.usecases.SettingsAccountUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_account.SettingsAccountViewModel
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsAccountViewModelTest: BaseViewModelTest<SettingsAccountViewModel>() {

    @MockK
    private lateinit var useCase: SettingsAccountUseCase

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    override fun createViewModel() = SettingsAccountViewModel(application, useCase)

    @Test
    fun signOutTest() {
        coEvery { useCase.signOut(resultMock) } answers {
            resultMock.invoke(Result.Success())
        }
        viewModel.signOut()
        coVerify { useCase.signOut(resultMock) }
        verify { resultMock.invoke(Result.Success()) }
        verify { viewModel.successLiveData.postValue(true) }
    }

    @Test
    fun changePasswordTest() {
        val newPassword = "newPassword"
        coEvery { useCase.changePassword(eq(TEST_PASSWORD), eq(newPassword), eq(resultMock)) } answers {
            resultMock.invoke(Result.Success())
        }
        viewModel.changePassword(TEST_PASSWORD, newPassword)
        coVerify { useCase.signOut(resultMock) }
        verify { resultMock.invoke(Result.Success()) }
        verify { viewModel.successLiveData.postValue(true) }
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