package com.tarasovvp.smartblocker.usecases

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class SettingsAccountUseCaseTest @Inject constructor(
    private val authRepository: AuthRepository
) {

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