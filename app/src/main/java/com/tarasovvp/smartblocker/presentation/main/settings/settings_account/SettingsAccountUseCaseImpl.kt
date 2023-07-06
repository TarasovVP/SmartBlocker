package com.tarasovvp.smartblocker.presentation.main.settings.settings_account

import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsAccountUseCase
import javax.inject.Inject

class SettingsAccountUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val realDataBaseRepository: RealDataBaseRepository
): SettingsAccountUseCase {

    override fun signOut(result: (Result<Unit>) -> Unit) = authRepository.signOut { authResult ->
        result.invoke(authResult)
    }

    override fun changePassword(currentPassword: String, newPassword: String, result: (Result<Unit>) -> Unit) = authRepository.changePassword(currentPassword, newPassword) { authResult ->
        result.invoke(authResult)
    }

    override fun deleteUser(result: (Result<Unit>) -> Unit) = realDataBaseRepository.deleteCurrentUser {
        authRepository.deleteUser(result)
    }
}