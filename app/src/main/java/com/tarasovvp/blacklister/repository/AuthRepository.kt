package com.tarasovvp.blacklister.repository

import android.content.Intent
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants

object AuthRepository {

    private val auth = BlackListerApp.instance?.auth
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun sendPasswordResetEmail(email: String, result: () -> Unit) {
        auth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke()
                } else {
                    sendExceptionBroadCast(task.exception?.localizedMessage.orEmpty())
                }
            }
    }

    fun signInWithEmailAndPassword(email: String, password: String, result: () -> Unit) {
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke()
                } else {
                    sendExceptionBroadCast(task.exception?.localizedMessage.orEmpty())
                }

            }
    }

    fun firebaseAuthWithGoogle(idToken: String, result: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke()
                } else {
                    sendExceptionBroadCast(task.exception?.localizedMessage.orEmpty())
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
                    sendExceptionBroadCast(createUserTask.exception?.localizedMessage.orEmpty())
                }
            }
    }

    fun changePassword(password: String, result: () -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user?.email.orEmpty(), password)
        user?.reauthenticate(credential)
            ?.addOnCompleteListener { changePasswordTask ->
                if (changePasswordTask.isSuccessful) {
                    result.invoke()
                } else {
                    sendExceptionBroadCast(changePasswordTask.exception?.localizedMessage.orEmpty())
                }
            }
    }

    fun deleteUser(result: () -> Unit) {
        currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result.invoke()
            } else {
                sendExceptionBroadCast(task.exception?.localizedMessage.orEmpty())
            }
        }
    }

    fun signOut(result: () -> Unit) {
        auth?.signOut()
        result.invoke()
    }

    private fun sendExceptionBroadCast(exception: String) {
        val intent = Intent(Constants.EXCEPTION)
        intent.putExtra(Constants.EXCEPTION, exception)
        BlackListerApp.instance?.sendBroadcast(intent)
    }
}