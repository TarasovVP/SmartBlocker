package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.usecases.SettingsAccountUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_account.SettingsAccountViewModel
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsAccountViewModelUnitTest: BaseViewModelUnitTest<SettingsAccountViewModel>() {

    @MockK
    private lateinit var useCase: SettingsAccountUseCase

    private var expectedResult = Result.Success<Unit>()

    override fun createViewModel() = SettingsAccountViewModel(application, useCase)

    @Test
    fun signOutTest() {
        every { application.isNetworkAvailable } returns true
        every { useCase.signOut(any()) } answers {
            val callback = firstArg<(Result<Unit>) -> Unit>()
            callback.invoke(expectedResult)
        }
        viewModel.signOut()
        verify { useCase.signOut(any()) }
        assertEquals(Unit, viewModel.successLiveData.getOrAwaitValue())
    }

    @Test
    fun changePasswordTest() {
        val newPassword = "newPassword"
        every { application.isNetworkAvailable } returns true
        every { useCase.changePassword(eq(TEST_PASSWORD), eq(newPassword), any()) } answers {
            val callback = thirdArg<(Result<Unit>) -> Unit>()
            callback.invoke(expectedResult)
        }
        viewModel.changePassword(TEST_PASSWORD, newPassword)
        verify { useCase.changePassword(eq(TEST_PASSWORD), eq(newPassword), any()) }
        assertEquals(Unit, viewModel.successChangePasswordLiveData.getOrAwaitValue())
    }

    @Test
    fun deleteUserTest() {
        every { application.isNetworkAvailable } returns true
        every { useCase.deleteUser(any()) } answers {
            val callback = firstArg<(Result<Unit>) -> Unit>()
            callback.invoke(expectedResult)
        }
        viewModel.deleteUser()
        verify { useCase.deleteUser(any()) }
        assertEquals(Unit, viewModel.successLiveData.getOrAwaitValue())
    }
}