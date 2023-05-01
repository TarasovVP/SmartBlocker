package com.tarasovvp.smartblocker.data.repositoryImpl

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.utils.extensions.sendExceptionBroadCast
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val smartBlockerApp: SmartBlockerApp?) : AuthRepository {

    override fun sendPasswordResetEmail(email: String, result: () -> Unit) {
        if (smartBlockerApp?.checkNetworkUnAvailable().isTrue()) return
        smartBlockerApp?.firebaseAuth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener {
                result.invoke()
            }?.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun signInWithEmailAndPassword(email: String, password: String, result: () -> Unit) {
        if (smartBlockerApp?.checkNetworkUnAvailable().isTrue()) return
        smartBlockerApp?.firebaseAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener {
                result.invoke()
            }?.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun signInWithGoogle(idToken: String, result: () -> Unit) {
        if (smartBlockerApp?.checkNetworkUnAvailable().isTrue()) return
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        smartBlockerApp?.firebaseAuth?.signInWithCredential(credential)
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
        if (smartBlockerApp?.checkNetworkUnAvailable().isTrue()) return
        smartBlockerApp?.firebaseAuth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener {
                result.invoke()
            }?.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun changePassword(currentPassword: String, newPassword: String, result: () -> Unit) {
        if (smartBlockerApp?.checkNetworkUnAvailable().isTrue()) return
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
        if (smartBlockerApp?.checkNetworkUnAvailable().isTrue()) return
        smartBlockerApp?.firebaseAuth?.currentUser?.delete()
            ?.addOnCompleteListener {
                result.invoke()
            }?.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun signOut(googleSignInClient: GoogleSignInClient, result: () -> Unit) {
        if (smartBlockerApp?.checkNetworkUnAvailable().isTrue()) return
        googleSignInClient.signOut().addOnCompleteListener {
            smartBlockerApp?.firebaseAuth?.signOut()
            result.invoke()
        }.addOnFailureListener { exception ->
            exception.localizedMessage.orEmpty().sendExceptionBroadCast()
        }
    }
}