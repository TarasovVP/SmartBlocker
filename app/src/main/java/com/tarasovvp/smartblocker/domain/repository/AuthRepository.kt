package com.tarasovvp.smartblocker.domain.repository

import com.google.android.gms.auth.api.signin.GoogleSignInClient

interface AuthRepository {

    fun sendPasswordResetEmail(email: String, result: () -> Unit)

    fun signInWithEmailAndPassword(email: String, password: String, result: () -> Unit)

    fun signInWithGoogle(idToken: String, result: () -> Unit)

    fun createUserWithEmailAndPassword(email: String, password: String, result: () -> Unit)

    fun changePassword(currentPassword: String, newPassword: String, result: () -> Unit)

    fun deleteUser(googleSignInClient: GoogleSignInClient, result: () -> Unit)

    fun signOut(googleSignInClient: GoogleSignInClient, result: () -> Unit)
}