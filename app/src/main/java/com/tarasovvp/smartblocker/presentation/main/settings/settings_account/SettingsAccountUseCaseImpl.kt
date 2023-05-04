package com.tarasovvp.smartblocker.presentation.main.settings.settings_account

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import com.tarasovvp.smartblocker.domain.usecase.SettingsAccountUseCase
import javax.inject.Inject

class SettingsAccountUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): SettingsAccountUseCase {

    override fun signOut(googleSignInClient: GoogleSignInClient, result: (OperationResult<Unit>) -> Unit) = authRepository.signOut(googleSignInClient) { operationResult ->
        result.invoke(operationResult)
    }

    override fun changePassword(currentPassword: String, newPassword: String, result: (OperationResult<Unit>) -> Unit) = authRepository.changePassword(currentPassword, newPassword) { operationResult ->
        result.invoke(operationResult)
    }

    override fun deleteUser(googleSignInClient: GoogleSignInClient, result: (OperationResult<Unit>) -> Unit) = authRepository.deleteUser(googleSignInClient) {
        authRepository.signOut(googleSignInClient, result)
    }
}