package com.tarasovvp.smartblocker.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.sendExceptionBroadCast

object AuthRepository {

    private val auth = SmartBlockerApp.instance?.auth
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun sendPasswordResetEmail(email: String, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        auth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke()
                } else {
                    task.exception?.localizedMessage.orEmpty().sendExceptionBroadCast()
                }
            }
    }

    fun signInWithEmailAndPassword(email: String, password: String, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke()
                } else {
                    task.exception?.localizedMessage.orEmpty().sendExceptionBroadCast()
                }

            }
    }

    fun signInWithGoogle(idToken: String, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke()
                } else {
                    task.exception?.localizedMessage.orEmpty().sendExceptionBroadCast()
                }
            }
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        result: () -> Unit,
    ) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { createUserTask ->
                if (createUserTask.isSuccessful) {
                    result.invoke()
                } else {
                    createUserTask.exception?.localizedMessage.orEmpty().sendExceptionBroadCast()
                }
            }
    }

    fun changePassword(currentPassword: String, newPassword: String, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user?.email.orEmpty(), currentPassword)
        user?.reauthenticateAndRetrieveData(credential)
            ?.addOnCompleteListener { passwordTask ->
                if (passwordTask.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener {
                        if (passwordTask.isSuccessful) {
                            result.invoke()
                        } else {
                            passwordTask.exception?.localizedMessage.orEmpty()
                                .sendExceptionBroadCast()
                        }
                    }
                } else {
                    passwordTask.exception?.localizedMessage.orEmpty().sendExceptionBroadCast()
                }
            }
    }

    fun deleteUser(result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result.invoke()
            } else {
                task.exception?.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
        }
    }

    fun signOut(result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        SmartBlockerApp.instance?.googleSignInClient?.signOut()
        auth?.signOut()
        result.invoke()
    }
}