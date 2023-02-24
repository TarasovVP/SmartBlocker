package com.tarasovvp.smartblocker.repository.interfaces

interface AuthRepository {

    fun sendPasswordResetEmail(email: String, result: () -> Unit)

    fun signInWithEmailAndPassword(email: String, password: String, result: () -> Unit)

    fun signInWithGoogle(idToken: String, result: () -> Unit)

    fun createUserWithEmailAndPassword(email: String, password: String, result: () -> Unit)

    fun changePassword(currentPassword: String, newPassword: String, result: () -> Unit)

    fun deleteUser(result: () -> Unit)

    fun signOut(result: () -> Unit)
}