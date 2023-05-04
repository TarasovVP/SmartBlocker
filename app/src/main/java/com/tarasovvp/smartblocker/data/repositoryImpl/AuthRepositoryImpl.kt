package com.tarasovvp.smartblocker.data.repositoryImpl

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.smartblocker.utils.extensions.sendExceptionBroadCast
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth, private val googleSignInClient: GoogleSignInClient) : AuthRepository {

    override fun sendPasswordResetEmail(email: String, result: () -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                result.invoke()
            }.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun signInWithEmailAndPassword(email: String, password: String, result: (String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                result.invoke(it.result.user?.email)
            }.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun signInWithGoogle(idToken: String, result: (String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                result.invoke(it.result.user?.email)
            }.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun createUserWithEmailAndPassword(email: String, password: String, result: (String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                result.invoke(it.result.user?.email)
            }.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun changePassword(currentPassword: String, newPassword: String, result: () -> Unit) {
        val user = firebaseAuth.currentUser
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

    override fun deleteUser(result: () -> Unit) {
        firebaseAuth.currentUser?.delete()?.addOnCompleteListener {
                result.invoke()
            }?.addOnFailureListener { exception ->
                exception.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun signOut(result: () -> Unit) {
        googleSignInClient.signOut().addOnCompleteListener {
           firebaseAuth.signOut()
            result.invoke()
        }.addOnFailureListener { exception ->
            exception.localizedMessage.orEmpty().sendExceptionBroadCast()
        }
    }
}