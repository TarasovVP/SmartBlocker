package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface AuthRepository {

    fun sendPasswordResetEmail(email: String, result: (Result<Unit>) -> Unit)

    fun signInWithEmailAndPassword(email: String, password: String, result: (Result<Unit>) -> Unit)

    fun signInWithGoogle(idToken: String, result: (Result<Unit>) -> Unit)

    fun createUserWithEmailAndPassword(email: String, password: String, result: (Result<Unit>) -> Unit)

    fun changePassword(currentPassword: String, newPassword: String, result: (Result<Unit>) -> Unit)

    fun deleteUser(result: (Result<Unit>) -> Unit)

    fun signOut(result: (Result<Unit>) -> Unit)
}