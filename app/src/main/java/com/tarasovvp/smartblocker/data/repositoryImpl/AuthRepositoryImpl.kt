package com.tarasovvp.smartblocker.data.repositoryImpl

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.sendExceptionBroadCast
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val auth: FirebaseAuth?) : AuthRepository {

    override fun sendPasswordResetEmail(email: String, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        auth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener {
                result.invoke()
            }?.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun signInWithEmailAndPassword(email: String, password: String, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener {
                result.invoke()
            }?.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun signInWithGoogle(idToken: String, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                result.invoke()
            }?.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        result: () -> Unit,
    ) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener {
                result.invoke()
            }?.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun changePassword(currentPassword: String, newPassword: String, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user?.email.orEmpty(), currentPassword)
        user?.reauthenticateAndRetrieveData(credential)
            ?.addOnCompleteListener { passwordTask ->
                user.updatePassword(newPassword).addOnCompleteListener {
                    if (passwordTask.isSuccessful) {
                        result.invoke()
                    } else {
                        passwordTask.exception?.localizedMessage.orEmpty()
                            .sendExceptionBroadCast()
                    }
                }
            }?.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun deleteUser(googleSignInClient: GoogleSignInClient, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        auth?.currentUser?.delete()
            ?.addOnCompleteListener {
                googleSignInClient.signOut()
                result.invoke()
            }?.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun signOut(googleSignInClient: GoogleSignInClient, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        googleSignInClient.signOut()
        auth?.signOut()
        result.invoke()
    }
}