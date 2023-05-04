package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult

interface LoginUseCase {

    fun sendPasswordResetEmail(email: String, result: (OperationResult<Unit>) -> Unit)

    fun signInWithEmailAndPassword(email: String, password: String, result: (OperationResult<String?>) -> Unit)

    fun firebaseAuthWithGoogle(idToken: String, result: (OperationResult<String?>) -> Unit)
}