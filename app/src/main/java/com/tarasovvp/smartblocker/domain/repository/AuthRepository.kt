package com.tarasovvp.smartblocker.domain.repository

import com.google.android.gms.auth.api.signin.GoogleSignInClient

interface AuthRepository {

    fun sendPasswordResetEmail(email: String, result: () -> Unit)

    fun signInWithEmailAndPassword(email: String, password: String, result: (String?) -> Unit)

    fun signInWithGoogle(idToken: String, result: (String?) -> Unit)

    fun createUserWithEmailAndPassword(email: String, password: String, result: (String?) -> Unit)

    fun changePassword(currentPassword: String, newPassword: String, result: () -> Unit)

    fun deleteUser(result: () -> Unit)

    fun signOut(result: () -> Unit)
}