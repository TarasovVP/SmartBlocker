package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsAccountUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_account.SettingsAccountUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SettingsAccountUseCaseUnitTest {

    @MockK
    private lateinit var authRepository: AuthRepository

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK
    private lateinit var dataStoreRepository: DataStoreRepository

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    private lateinit var settingsAccountUseCase: SettingsAccountUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        settingsAccountUseCase = SettingsAccountUseCaseImpl(authRepository, realDataBaseRepository, dataStoreRepository)
    }

    @Test
    fun signOutTest() {
        every { authRepository.signOut(any()) } answers {
            resultMock.invoke(Result.Success())
        }
        settingsAccountUseCase.signOut(resultMock)
        verify(exactly = 1) { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun changePasswordTest() {
        val newPassword = "newPassword"
        every { authRepository.changePassword(eq(TEST_PASSWORD), eq(newPassword), any()) } answers {
            resultMock.invoke(Result.Success())
        }
        settingsAccountUseCase.changePassword(TEST_PASSWORD, newPassword, resultMock)
        verify(exactly = 1) { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun deleteUserTest() {
        every { realDataBaseRepository.deleteCurrentUser(any()) } answers {
            resultMock.invoke(Result.Success())
        }
        every { authRepository.deleteUser(any()) } answers {
            resultMock.invoke(Result.Success())
        }
        settingsAccountUseCase.deleteUser(resultMock)
        verify(exactly = 1) { resultMock.invoke(Result.Success()) }
    }
}