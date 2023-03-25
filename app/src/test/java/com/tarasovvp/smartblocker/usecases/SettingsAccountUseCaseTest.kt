package com.tarasovvp.smartblocker.usecases

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_account.SettingsAccountUseCaseImpl
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SettingsAccountUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var settingsAccountUseCaseImpl: SettingsAccountUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        settingsAccountUseCaseImpl = SettingsAccountUseCaseImpl(authRepository)
    }

    fun signOut(googleSignInClient: GoogleSignInClient, result: () -> Unit) = authRepository.signOut(googleSignInClient) {
        result.invoke()
    }

    fun changePassword(currentPassword: String, newPassword: String, result: () -> Unit) = authRepository.changePassword(currentPassword, newPassword) {
        result.invoke()
    }

    fun deleteUser(googleSignInClient: GoogleSignInClient, result: () -> Unit) = authRepository.deleteUser(googleSignInClient) {
        result.invoke()
    }
}