package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface LoginUseCase {

    fun sendPasswordResetEmail(email: String, result: (Result<Unit>) -> Unit)

    fun signInWithEmailAndPassword(email: String, password: String, result: (Result<Unit>) -> Unit)

    fun firebaseAuthWithGoogle(idToken: String, result: (Result<Unit>) -> Unit)
}