package com.tarasovvp.smartblocker.domain.repository

import com.google.firebase.auth.AuthCredential
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface AuthRepository {

    fun sendPasswordResetEmail(email: String, result: (Result<Unit>) -> Unit)

    fun fetchSignInMethodsForEmail(email: String, result: (Result<List<String>>) -> Unit)

    fun signInWithEmailAndPassword(email: String, password: String, result: (Result<Unit>) -> Unit)

    fun signInWithGoogle(idToken: String, result: (Result<Unit>) -> Unit)

    fun signInAnonymously(result: (Result<Unit>) -> Unit)

    fun createUserWithEmailAndPassword(email: String, password: String, result: (Result<String>) -> Unit)

    fun changePassword(currentPassword: String, newPassword: String, result: (Result<Unit>) -> Unit)

    fun reAuthenticate(authCredential: AuthCredential, result: (Result<Unit>) -> Unit)

    fun deleteUser(result: (Result<Unit>) -> Unit)

    fun signOut(result: (Result<Unit>) -> Unit)
}