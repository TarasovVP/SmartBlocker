package com.tarasovvp.smartblocker.usecases

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.usecase.SettingsAccountUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_account.SettingsAccountUseCaseImpl
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class SettingsAccountUseCaseTest {

    @MockK
    private lateinit var authRepository: AuthRepository

    @MockK
    private lateinit var googleSignInClient: GoogleSignInClient

    @MockK(relaxed = true)
    private lateinit var resultMock: () -> Unit

    private lateinit var settingsAccountUseCase: SettingsAccountUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        settingsAccountUseCase = SettingsAccountUseCaseImpl(authRepository)
    }

    @Test
    fun signOutTest() {
        every { authRepository.signOut(eq(googleSignInClient), any()) } answers {
            resultMock.invoke()
        }
        settingsAccountUseCase.signOut(googleSignInClient, resultMock)
        verify(exactly = 1) { resultMock.invoke() }
    }

    @Test
    fun changePasswordTest() {
        val newPassword = "newPassword"
        every { authRepository.changePassword(eq(TEST_PASSWORD), eq(newPassword), any()) } answers {
            resultMock.invoke()
        }
        settingsAccountUseCase.changePassword(TEST_PASSWORD, newPassword, resultMock)
        verify(exactly = 1) { resultMock.invoke() }
    }

    @Test
    fun deleteUserTest() {
        every { authRepository.signOut(eq(googleSignInClient), any()) } answers {
            resultMock.invoke()
        }
        every { authRepository.deleteUser(eq(googleSignInClient), any()) } answers {
            resultMock.invoke()
        }
        settingsAccountUseCase.deleteUser(googleSignInClient, resultMock)
        verify(exactly = 1) { resultMock.invoke() }
    }
}