package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface LoginUseCase {

    fun sendPasswordResetEmail(email: String, result: (Result<Unit>) -> Unit)

    fun fetchSignInMethodsForEmail(email: String, result: (Result<List<String>>) -> Unit)

    fun signInWithEmailAndPassword(email: String, password: String, result: (Result<Unit>) -> Unit)

    fun signInAuthWithGoogle(idToken: String, result: (Result<Unit>) -> Unit)

    fun signInAnonymously(result: (Result<Unit>) -> Unit)
}