package com.tarasovvp.smartblocker.domain.usecase

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult

interface SettingsAccountUseCase {

    fun signOut(googleSignInClient: GoogleSignInClient, result: (OperationResult<Unit>) -> Unit)

    fun changePassword(currentPassword: String, newPassword: String, result: (OperationResult<Unit>) -> Unit)

    fun deleteUser(googleSignInClient: GoogleSignInClient, result: (OperationResult<Unit>) -> Unit)
}