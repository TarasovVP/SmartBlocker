package com.tarasovvp.smartblocker.repositoryImpl

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.sendExceptionBroadCast
import com.tarasovvp.smartblocker.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val auth: FirebaseAuth?) : AuthRepository {

    override fun sendPasswordResetEmail(email: String, result: () -> Unit) {
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

    override fun signInWithEmailAndPassword(email: String, password: String, result: () -> Unit) {
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

    override fun signInWithGoogle(idToken: String, result: () -> Unit) {
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

    override fun createUserWithEmailAndPassword(
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

    override fun changePassword(currentPassword: String, newPassword: String, result: () -> Unit) {
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

    override fun deleteUser(googleSignInClient: GoogleSignInClient, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        auth?.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                googleSignInClient.signOut()
                result.invoke()
            } else {
                task.exception?.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
        }
    }

    override fun signOut(googleSignInClient: GoogleSignInClient, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        googleSignInClient.signOut()
        auth?.signOut()
        result.invoke()
    }
}