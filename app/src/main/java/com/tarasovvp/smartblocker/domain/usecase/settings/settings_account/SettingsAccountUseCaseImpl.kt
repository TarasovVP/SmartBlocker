package com.tarasovvp.smartblocker.domain.usecase.settings.settings_account

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import javax.inject.Inject

class SettingsAccountUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): SettingsAccountUseCase {

    override fun signOut(googleSignInClient: GoogleSignInClient, result: () -> Unit) = authRepository.signOut(googleSignInClient) {
        result.invoke()
    }

    override fun changePassword(currentPassword: String, newPassword: String, result: () -> Unit) = authRepository.changePassword(currentPassword, newPassword) {
        result.invoke()
    }

    override fun deleteUser(googleSignInClient: GoogleSignInClient, result: () -> Unit) = authRepository.deleteUser(googleSignInClient) {
        authRepository.signOut(googleSignInClient, result)
    }
}