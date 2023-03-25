package com.tarasovvp.smartblocker.repositories

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.sendExceptionBroadCast
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest @Inject constructor(private val auth: FirebaseAuth?) {

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

    fun deleteUser(googleSignInClient: GoogleSignInClient, result: () -> Unit) {
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

    fun signOut(googleSignInClient: GoogleSignInClient, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        googleSignInClient.signOut()
        auth?.signOut()
        result.invoke()
    }
}