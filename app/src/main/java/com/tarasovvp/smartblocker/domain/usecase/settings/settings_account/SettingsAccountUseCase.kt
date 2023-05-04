package com.tarasovvp.smartblocker.domain.usecase.settings.settings_account

interface SettingsAccountUseCase {

    fun signOut(result: () -> Unit)

    fun changePassword(currentPassword: String, newPassword: String, result: () -> Unit)

    fun deleteUser(result: () -> Unit)
}