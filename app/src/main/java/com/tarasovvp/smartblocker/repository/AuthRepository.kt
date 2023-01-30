package com.tarasovvp.smartblocker.repository

import android.content.Intent
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.constants.Constants.EXCEPTION
import com.tarasovvp.smartblocker.extensions.isNotTrue
import com.tarasovvp.smartblocker.extensions.sendExceptionBroadCast

object AuthRepository {

    private val auth = SmartBlockerApp.instance?.auth
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun sendPasswordResetEmail(email: String, result: () -> Unit) {
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
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user?.email.orEmpty(), currentPassword)
        user?.reauthenticateAndRetrieveData(credential)
            ?.addOnCompleteListener { passwordTask ->
                if (passwordTask.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener {
                        if (passwordTask.isSuccessful) {
                            result.invoke()
                        } else {
                            passwordTask.exception?.localizedMessage.orEmpty().sendExceptionBroadCast()
                        }
                    }
                } else {
                    passwordTask.exception?.localizedMessage.orEmpty().sendExceptionBroadCast()
                }
            }
    }

    fun deleteUser(result: () -> Unit) {
        SmartBlockerApp.instance?.checkNetworkAvailable()
        currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result.invoke()
            } else {
                task.exception?.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
        }
    }

    fun signOut(result: () -> Unit) {
        SmartBlockerApp.instance?.checkNetworkAvailable()
        SmartBlockerApp.instance?.googleSignInClient?.signOut()
        auth?.signOut()
        result.invoke()
    }
}