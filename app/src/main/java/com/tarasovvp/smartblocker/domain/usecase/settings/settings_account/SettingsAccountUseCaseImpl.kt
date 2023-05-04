package com.tarasovvp.smartblocker.domain.usecase.settings.settings_account

import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import javax.inject.Inject

class SettingsAccountUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): SettingsAccountUseCase {

    override fun signOut(result: () -> Unit) = authRepository.signOut {
        result.invoke()
    }

    override fun changePassword(currentPassword: String, newPassword: String, result: () -> Unit) = authRepository.changePassword(currentPassword, newPassword) {
        result.invoke()
    }

    override fun deleteUser(result: () -> Unit) = authRepository.deleteUser {
        authRepository.signOut(result)
    }
}