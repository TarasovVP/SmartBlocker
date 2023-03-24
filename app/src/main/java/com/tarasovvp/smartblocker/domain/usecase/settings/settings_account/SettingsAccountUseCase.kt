package com.tarasovvp.smartblocker.domain.usecase.settings.settings_account

import com.google.android.gms.auth.api.signin.GoogleSignInClient

interface SettingsAccountUseCase {

    fun signOut(googleSignInClient: GoogleSignInClient, result: () -> Unit)

    fun changePassword(currentPassword: String, newPassword: String, result: () -> Unit)

    fun deleteUser(googleSignInClient: GoogleSignInClient, result: () -> Unit)
}