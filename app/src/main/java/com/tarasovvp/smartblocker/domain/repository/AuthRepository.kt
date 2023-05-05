package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult

interface AuthRepository {

    fun sendPasswordResetEmail(email: String, result: (OperationResult<Unit>) -> Unit)

    fun signInWithEmailAndPassword(email: String, password: String, result: (OperationResult<Unit>) -> Unit)

    fun signInWithGoogle(idToken: String, result: (OperationResult<Unit>) -> Unit)

    fun createUserWithEmailAndPassword(email: String, password: String, result: (OperationResult<Unit>) -> Unit)

    fun changePassword(currentPassword: String, newPassword: String, result: (OperationResult<Unit>) -> Unit)

    fun deleteUser(result: (OperationResult<Unit>) -> Unit)

    fun signOut(result: (OperationResult<Unit>) -> Unit)
}