package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult

interface SettingsAccountUseCase {

    fun signOut(gresult: (OperationResult<Unit>) -> Unit)

    fun changePassword(currentPassword: String, newPassword: String, result: (OperationResult<Unit>) -> Unit)

    fun deleteUser(result: (OperationResult<Unit>) -> Unit)
}