package com.tarasovvp.smartblocker.domain.usecase.authorization.login

interface LoginUseCase {

    fun sendPasswordResetEmail(email: String, result: () -> Unit)

    fun signInWithEmailAndPassword(email: String, password: String, result: () -> Unit)

    fun firebaseAuthWithGoogle(idToken: String, result: () -> Unit)
}